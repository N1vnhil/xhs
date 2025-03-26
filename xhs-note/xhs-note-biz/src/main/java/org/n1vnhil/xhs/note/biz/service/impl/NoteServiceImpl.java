package org.n1vnhil.xhs.note.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.n1vnhil.framework.common.enums.StatusEnum;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;
import org.n1vnhil.xhs.note.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteDO;
import org.n1vnhil.xhs.note.biz.domain.dataobject.TopicDO;
import org.n1vnhil.xhs.note.biz.domain.mapper.NoteDOMapper;
import org.n1vnhil.xhs.note.biz.domain.mapper.TopicDOMapper;
import org.n1vnhil.xhs.note.biz.enums.NoteStatusEnum;
import org.n1vnhil.xhs.note.biz.enums.NoteTypeEnum;
import org.n1vnhil.xhs.note.biz.enums.NoteVisibleEnum;
import org.n1vnhil.xhs.note.biz.enums.ResponseCodeEnum;
import org.n1vnhil.xhs.note.biz.model.vo.FindNoteDetailReqVO;
import org.n1vnhil.xhs.note.biz.model.vo.FindNoteDetailRspVO;
import org.n1vnhil.xhs.note.biz.model.vo.PublishNoteReqVO;
import org.n1vnhil.xhs.note.biz.model.vo.UpdateNoteReqVO;
import org.n1vnhil.xhs.note.biz.rpc.DistributedIdGeneratorRpcService;
import org.n1vnhil.xhs.note.biz.rpc.KvRpcService;
import org.n1vnhil.xhs.note.biz.rpc.UserRpcService;
import org.n1vnhil.xhs.note.biz.service.NoteService;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NoteServiceImpl implements NoteService {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private UserRpcService userRpcService;

    @Autowired
    private KvRpcService kvRpcService;

    @Autowired
    private DistributedIdGeneratorRpcService distributedIdGeneratorRpcService;

    @Autowired
    private TopicDOMapper topicDOMapper;

    @Autowired
    private NoteDOMapper noteDOMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Cache<Long, String> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(10000) // 设置初始容量为 10000 个条目
            .maximumSize(10000) // 设置缓存的最大容量为 10000 个条目
            .expireAfterWrite(1, TimeUnit.HOURS) // 设置缓存条目在写入后 1 小时过期
            .build();

    @Override
    public Response<?> publishNote(PublishNoteReqVO publishNoteReqVO) {
        Integer type = publishNoteReqVO.getType();
        NoteTypeEnum noteTypeEnum = NoteTypeEnum.valueOf(type);

        if(Objects.isNull(noteTypeEnum)) {
            throw new BizException(ResponseCodeEnum.NOTE_TYPE_ERROR);
        }

        String imageUris = null;
        String videoUri = null;
        switch (noteTypeEnum)  {
            // 图文笔记
            case IMAGE_TEXT -> {
                List<String> imageUriList = publishNoteReqVO.getImgUris();
                Preconditions.checkArgument(CollUtil.isNotEmpty(imageUriList), "笔记图片不能为空");
                Preconditions.checkArgument(imageUriList.size() <= 8, "笔记图片不能超过8张");
                imageUris = StringUtils.join(imageUriList, ",");
            }

            // 视频笔记
            case VIDEO -> {
                videoUri = publishNoteReqVO.getVideoUri();
                Preconditions.checkArgument(StringUtils.isNotBlank(videoUri), "视频不能为空");
            }
        }

        String snowflakeId = distributedIdGeneratorRpcService.getSnowflakeId();
        String contentId = null;
        String content = publishNoteReqVO.getContent();

        boolean empty = true;
        if(StringUtils.isNotBlank(content)) {
            empty = false;
            contentId = UUID.randomUUID().toString();
            boolean saved = kvRpcService.saveNoteContent(contentId, content);
            if(!saved) throw new BizException(ResponseCodeEnum.NOTE_PUBLISH_FAIL);
        }

        Long topicId = publishNoteReqVO.getTopicId();
        String topicName = null;
        TopicDO topicDO = topicDOMapper.selectTopicById(topicId);
        if(Objects.nonNull(topicId)) {
            topicName = topicDO.getName();
        }


        Long creatorId = LoginUserContextHolder.getLoginUserId();
        NoteDO noteDO = NoteDO.builder()
                .id(Long.valueOf(snowflakeId))
                .contentEmpty(empty)
                .title(publishNoteReqVO.getTitle())
                .creatorId(creatorId)
                .topicId(topicId)
                .topicName(topicName)
                .top(Boolean.FALSE)
                .type(type)
                .imgUris(imageUris)
                .videoUri(videoUri)
                .visible(NoteVisibleEnum.PUBLIC.getCode())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .contentUuid(contentId)
                .status(NoteStatusEnum.NORMAL.getCode())
                .build();

        try {
            noteDOMapper.insert(noteDO);
        } catch (Exception e) {
            log.error("==========> 笔记存储失败：", e);
            if(StringUtils.isNotBlank(contentId)) {
                kvRpcService.deleteNoteContent(contentId);
            }
        }

        return Response.success();
    }

    @Override
    public Response<FindNoteDetailRspVO> findNoteDetail(FindNoteDetailReqVO findNoteDetailReqVO) {
        Long noteId = findNoteDetailReqVO.getId();
        Long userId = LoginUserContextHolder.getLoginUserId();
        String key = RedisKeyConstants.buildNoteDetailKey(noteId);

        // 查询本地缓存
        String noteDOStr = LOCAL_CACHE.getIfPresent(noteId);
        if(StringUtils.isNotBlank(noteDOStr)) {
            FindNoteDetailRspVO findNoteDetailRspVO = JsonUtils.parseObject(noteDOStr, FindNoteDetailRspVO.class);
            checkNoteVisibleVO(userId, findNoteDetailRspVO);
            return Response.success(findNoteDetailRspVO);
        }

        // 查询 redis
        noteDOStr = (String) redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(noteDOStr)) {
            FindNoteDetailRspVO findNoteDetailRspVO = JsonUtils.parseObject(noteDOStr, FindNoteDetailRspVO.class);
            threadPoolTaskExecutor.execute(() -> {
                LOCAL_CACHE.put(noteId, Objects.isNull(findNoteDetailRspVO)
                        ? "null" : JsonUtils.toJsonString(findNoteDetailRspVO));
            });
            checkNoteVisibleVO(userId, findNoteDetailRspVO);
            return Response.success(findNoteDetailRspVO);
        }

        // 查询数据库
        NoteDO noteDO = noteDOMapper.selectNoteById(noteId);

        if(Objects.isNull(noteDO)) {
            threadPoolTaskExecutor.execute(() -> {
                long expireTime = 60 + RandomUtil.randomInt(60);
                redisTemplate.opsForValue().set(key, "null", expireTime, TimeUnit.SECONDS);
            });
            throw new BizException(ResponseCodeEnum.NOTE_NOT_FOUND);
        }

        Long creatorId = noteDO.getCreatorId();
        checkNoteVisible(noteDO.getVisible(), userId, creatorId);

        String content = null;
        if(Objects.equals(noteDO.getContentEmpty(), Boolean.FALSE)) {
            content = kvRpcService.getNoteContent(noteDO.getContentUuid());
        }

        List<String> imgUris = null;
        String imgUriStr = noteDO.getImgUris();
        if(Objects.equals(noteDO.getType(), NoteTypeEnum.IMAGE_TEXT.getCode())
                && StringUtils.isNotBlank(imgUriStr)) {
            imgUris = List.of(imgUriStr.split(","));
        }

        FindUserByIdRspDTO creator = userRpcService.getUserById(creatorId);
        FindNoteDetailRspVO findNoteDetailRspVO = FindNoteDetailRspVO.builder()
                .id(noteId)
                .type(noteDO.getType())
                .title(noteDO.getTitle())
                .content(content)
                .imgUris(imgUris)
                .topicId(noteDO.getTopicId())
                .topicName(noteDO.getTopicName())
                .creatorName(creator.getNickname())
                .creatorId(creatorId)
                .avatar(creator.getAvatar())
                .videoUri(noteDO.getVideoUri())
                .updateTime(noteDO.getUpdateTime())
                .visible(noteDO.getVisible())
                .build();

        threadPoolTaskExecutor.execute(() -> {
            String json = JsonUtils.toJsonString(findNoteDetailRspVO);
            long expireTime = 60*60*24 + RandomUtil.randomInt(60*60*24);
            redisTemplate.opsForValue().set(key, json, expireTime, TimeUnit.SECONDS);
        });

        return Response.success(findNoteDetailRspVO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<?> updateNote(UpdateNoteReqVO updateNoteReqVO) {
        Long noteId = updateNoteReqVO.getId();
        NoteTypeEnum noteTypeEnum = NoteTypeEnum.valueOf(updateNoteReqVO.getType());
        if(Objects.isNull(noteTypeEnum)) {
            throw new BizException(ResponseCodeEnum.NOTE_TYPE_ERROR);
        }

        String imgUris = null;
        String videoUri = null;
        switch (noteTypeEnum) {
            case IMAGE_TEXT -> {
                List<String> uris = updateNoteReqVO.getImgUris();
                if(Objects.nonNull(uris)) {
                    Preconditions.checkArgument(uris.size() <= 8, "笔记图片不能超过8张");
                    imgUris = StringUtils.join(uris, ",");
                }
            }

            case VIDEO -> {
                videoUri = updateNoteReqVO.getVideoUri();
            }
        }

        Long topicId = updateNoteReqVO.getTopicId();
        String topicName = null;
        if(Objects.nonNull(topicId)) {
            TopicDO topicDO = topicDOMapper.selectTopicById(topicId);
            if(Objects.isNull(topicDO)) throw new BizException(ResponseCodeEnum.NOTE_TOPIC_NOT_FOUND);
            topicName = topicDO.getName();
        }

        String content = updateNoteReqVO.getContent();
        NoteDO noteDO = NoteDO.builder()
                .id(noteId)
                .contentEmpty(StringUtils.isBlank(content))
                .type(updateNoteReqVO.getType())
                .title(updateNoteReqVO.getTitle())
                .topicId(updateNoteReqVO.getTopicId())
                .topicName(topicName)
                .imgUris(imgUris)
                .videoUri(videoUri)
                .updateTime(LocalDateTime.now())
                .build();
        noteDOMapper.update(noteDO);

        String key = RedisKeyConstants.buildNoteDetailKey(noteId);
        redisTemplate.delete(key);
        LOCAL_CACHE.invalidate(noteId);

        NoteDO noteDO1 = noteDOMapper.selectNoteById(noteId);
        String contentUuid = noteDO1.getContentUuid();

        // 笔记内容是否更新成功
        boolean isUpdateContentSuccess = false;
        if (StringUtils.isBlank(content)) {
            isUpdateContentSuccess = kvRpcService.deleteNoteContent(contentUuid);
        } else {
            isUpdateContentSuccess = kvRpcService.saveNoteContent(contentUuid, content);
        }

        // 如果更新失败，抛出业务异常，回滚事务
        if (!isUpdateContentSuccess) {
            throw new BizException(ResponseCodeEnum.NOTE_UPDATE_FAIL);
        }

        return Response.success();
    }

    private void checkNoteVisible(Integer visible, Long userId, Long creatorId) {
        if(Objects.equals(visible, NoteVisibleEnum.PRIVATE.getCode())
                && !Objects.equals(userId, creatorId)) {
            throw new BizException(ResponseCodeEnum.NOTE_PRIVATE);
        }
    }

    private void checkNoteVisibleVO(Long userId, FindNoteDetailRspVO findNoteDetailRspVO) {
        if(Objects.nonNull(findNoteDetailRspVO)) {
            Integer visible = findNoteDetailRspVO.getVisible();
            checkNoteVisible(visible, userId, findNoteDetailRspVO.getCreatorId());
        }
    }
}
