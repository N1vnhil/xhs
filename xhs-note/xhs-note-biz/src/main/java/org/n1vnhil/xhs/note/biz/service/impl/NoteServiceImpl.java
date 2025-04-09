package org.n1vnhil.xhs.note.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.alibaba.nacos.shaded.io.grpc.internal.JsonUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.checkerframework.checker.units.qual.C;
import org.n1vnhil.framework.common.enums.DeletedEnum;
import org.n1vnhil.framework.common.enums.StatusEnum;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.common.util.DateUtils;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.framework.context.filter.HeadUserId2ContextFilter;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;
import org.n1vnhil.xhs.note.biz.constant.MQConstants;
import org.n1vnhil.xhs.note.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteDO;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteLikeDO;
import org.n1vnhil.xhs.note.biz.domain.dataobject.TopicDO;
import org.n1vnhil.xhs.note.biz.domain.mapper.NoteDOMapper;
import org.n1vnhil.xhs.note.biz.domain.mapper.NoteLikeDOMapper;
import org.n1vnhil.xhs.note.biz.domain.mapper.TopicDOMapper;
import org.n1vnhil.xhs.note.biz.enums.*;
import org.n1vnhil.xhs.note.biz.model.vo.*;
import org.n1vnhil.xhs.note.biz.rpc.DistributedIdGeneratorRpcService;
import org.n1vnhil.xhs.note.biz.rpc.KvRpcService;
import org.n1vnhil.xhs.note.biz.rpc.UserRpcService;
import org.n1vnhil.xhs.note.biz.service.NoteService;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private NoteLikeDOMapper noteLikeDOMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private static final Cache<Long, String> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(10000) // 设置初始容量为 10000 个条目
            .maximumSize(10000) // 设置缓存的最大容量为 10000 个条目
            .expireAfterWrite(1, TimeUnit.HOURS) // 设置缓存条目在写入后 1 小时过期
            .build();
    @Autowired
    private NoteService noteService;

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

        // 检查可见性
        Integer visible = noteDO.getVisible();
        checkNoteVisible(visible, userId, noteDO.getCreatorId());

        // 下游服务并发执行优化
        // 调用用户服务
        CompletableFuture<FindUserByIdRspDTO> userResultFuture = CompletableFuture.supplyAsync(
                () -> userRpcService.getUserById(userId), threadPoolTaskExecutor
        );

        // 调用kv存储服务
        CompletableFuture<String> contentResultFuture = CompletableFuture.completedFuture(null);
        if(Objects.equals(noteDO.getContentEmpty(), Boolean.FALSE)) {
            contentResultFuture = CompletableFuture.supplyAsync(
                () -> kvRpcService.getNoteContent(noteDO.getContentUuid()), threadPoolTaskExecutor
            );
        }

        CompletableFuture<String> finalContentResult=  contentResultFuture;
        CompletableFuture<FindNoteDetailRspVO> resultFuture = CompletableFuture
                .allOf(contentResultFuture, userResultFuture)
                .thenApply(s -> {
                    FindUserByIdRspDTO user = userResultFuture.join();
                    String content =  finalContentResult.join();

                    List<String> imgUris = null;
                    String imgUriStr = noteDO.getImgUris();
                    if(Objects.equals(noteDO.getType(), NoteTypeEnum.IMAGE_TEXT.getCode())
                            && StringUtils.isNotBlank(imgUriStr)) {
                        imgUris = List.of(imgUriStr.split(","));
                    }

                    return FindNoteDetailRspVO.builder()
                            .id(noteId)
                            .type(noteDO.getType())
                            .title(noteDO.getTitle())
                            .content(content)
                            .imgUris(imgUris)
                            .topicId(noteDO.getTopicId())
                            .topicName(noteDO.getTopicName())
                            .creatorName(user.getNickname())
                            .creatorId(user.getId())
                            .avatar(user.getAvatar())
                            .videoUri(noteDO.getVideoUri())
                            .updateTime(noteDO.getUpdateTime())
                            .visible(noteDO.getVisible())
                            .build();
                });


        FindNoteDetailRspVO findNoteDetailRspVO = resultFuture.join();
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
        checkOperatePermission(noteId);

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

        // 删除redis缓存
        String key = RedisKeyConstants.buildNoteDetailKey(noteId);
        redisTemplate.delete(key);

        // MQ广播删除所有实例本地缓存
        rocketMQTemplate.syncSend(MQConstants.TOPIC_DELETE_NOTE_LOCAL_CACHE, noteId);
        log.info("==========> MQ: 发送删除本地缓存");

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

    @Override
    public void deleteLocalNoteCache(Long noteId) {
        LOCAL_CACHE.invalidate(noteId);
    }

    @Override
    public Response<?> deleteNote(DeleteNoteReqVO deleteNoteReqVO) {
        Long noteId = deleteNoteReqVO.getId();
        checkOperatePermission(noteId);
        NoteDO noteDO = NoteDO.builder()
                .id(noteId)
                .status(NoteStatusEnum.DELETED.getCode())
                .updateTime(LocalDateTime.now())
                .build();

        // 数据库逻辑删除
        int cnt = noteDOMapper.update(noteDO);
        if(cnt == 0) {
            throw new BizException(ResponseCodeEnum.NOTE_NOT_FOUND);
        }

        // 删除redis缓存
        String key = RedisKeyConstants.buildNoteDetailKey(noteId);
        redisTemplate.delete(key);

        // MQ广播删除本地缓存
        rocketMQTemplate.syncSend(MQConstants.TOPIC_DELETE_NOTE_LOCAL_CACHE, noteId);
        log.info("==========> MQ: 广播删除缓存，noteId: {}", noteId);
        return Response.success();
    }

    @Override
    public Response<?> setOnlyMe(OnlyMeVisibleReqVO onlyMeVisibleReqVO) {
        Long noteId = onlyMeVisibleReqVO.getId();
        checkOperatePermission(noteId);

        NoteDO noteDO = NoteDO.builder()
                .id(noteId)
                .visible(NoteVisibleEnum.PRIVATE.getCode())
                .updateTime(LocalDateTime.now())
                .build();
        int cnt = noteDOMapper.update(noteDO);

        if(cnt == 0) throw new BizException(ResponseCodeEnum.NOTE_CANNOT_ONLY_ME);

        // 删除redis缓存
        String key = RedisKeyConstants.buildNoteDetailKey(noteId);
        redisTemplate.delete(key);

        // MQ广播删除本地缓存
        rocketMQTemplate.syncSend(MQConstants.TOPIC_DELETE_NOTE_LOCAL_CACHE, noteId);
        log.info("==========> MQ: 广播删除缓存，noteId: {}", noteId);
        return Response.success();
    }

    @Override
    public Response<?> setTopStatus(TopNoteReqVO topNoteReqVO) {
        Long noteId = topNoteReqVO.getId();
        Long userId = LoginUserContextHolder.getLoginUserId();
        NoteDO noteDO = NoteDO.builder()
                .id(noteId)
                .updateTime(LocalDateTime.now())
                .top(topNoteReqVO.getTop())
                .creatorId(userId)
                .build();
        int cnt = noteDOMapper.updateTop(noteDO);
        if(cnt == 0) throw new BizException(ResponseCodeEnum.NOTE_CANT_OPERATE);

        redisTemplate.delete(RedisKeyConstants.buildNoteDetailKey(noteId));
        rocketMQTemplate.syncSend(MQConstants.TOPIC_DELETE_NOTE_LOCAL_CACHE, noteId);
        return Response.success();
    }

    @Override
    public Response<?> likeNote(LikeNoteReqVO likeNoteReqVO) {
        // 1. 笔记判空
        Long noteId = likeNoteReqVO.getId();
        Long userId = LoginUserContextHolder.getLoginUserId();
        checkNoteExist(noteId);

        // 2. 判断是否点赞
        String bloomFilterKey = RedisKeyConstants.buildBloomUserNoteLikeListKey(noteId);
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/bloom_note_like_check.lua")));
        script.setResultType(Long.class);
        Long result = redisTemplate.execute(script, Collections.singletonList(bloomFilterKey), noteId);
        NoteLikeLuaResultEnum noteLikeLuaResultEnum = NoteLikeLuaResultEnum.valueOf(result);

        String noteLikeZsetKey = RedisKeyConstants.buildUserNoteLikeZsetKey(userId);
        switch (noteLikeLuaResultEnum) {
            // 布隆过滤器判断已点赞
            case NOTE_LIKED -> {
                Double score = redisTemplate.opsForZSet().score(noteLikeZsetKey, noteId);
                if(Objects.nonNull(score)) {
                    throw new BizException(ResponseCodeEnum.NOTE_ALREADY_LIKED);
                }

                // 若布隆过滤器误判
                int count = noteLikeDOMapper.selectCountByUserIdAndNoteId(userId, noteId);
                if(count > 0) {
                    asynInitUserNoteLikesZset(userId, noteLikeZsetKey);
                    throw new BizException(ResponseCodeEnum.NOTE_ALREADY_LIKED);
                }
            }

            // 未点赞，更新布隆过滤器
            case NOT_EXIST -> {
                int count = noteLikeDOMapper.selectCountByUserIdAndNoteId(userId, noteId);
                long expireTime = getRandomExpireTime();
                if(count > 0) {
                    asynBatchAddNoteLikeAndExpire(userId, expireTime, bloomFilterKey);
                    throw new BizException(ResponseCodeEnum.NOTE_ALREADY_LIKED);
                }

                script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/bloom_add_note_like_and_expire.lua")));
                script.setResultType(Long.class);
                redisTemplate.execute(script, Collections.singletonList(bloomFilterKey), noteId, expireTime);
            }
        }

        // 3. 更新zset
        LocalDateTime now = LocalDateTime.now();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/note_like_zset_check_and_update.lua")));
        script.setResultType(Long.class);
        result = redisTemplate.execute(script, Collections.singletonList(noteLikeZsetKey), noteId, DateUtils.localDateTime2Timestamp(now));

        // zset不存在，重新初始化zset
        if(Objects.equals(result, NoteLikeLuaResultEnum.NOT_EXIST)) {
           List<NoteLikeDO> noteLikeDOS = noteLikeDOMapper.selectByUserIdAndLimit(userId, 100);
           if(CollUtil.isNotEmpty(noteLikeDOS)) {
               Object[] luaArgs = buildNoteLikeZsetLuaArgs(noteLikeDOS, getRandomExpireTime());
               DefaultRedisScript<Long> script1 = new DefaultRedisScript<>();
               script1.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/batch_add_note_like_zset_and_expire.lua")));
               script1.setResultType(Long.class);
               redisTemplate.execute(script1, Collections.singletonList(noteLikeZsetKey), luaArgs);
               redisTemplate.execute(script, Collections.singletonList(noteLikeZsetKey), DateUtils.localDateTime2Timestamp(now));
           }

        }

        // 4. 发送mq
        return null;
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

    private void checkOperatePermission(Long noteId) {
        Long userId = LoginUserContextHolder.getLoginUserId();
        NoteDO noteDO = noteDOMapper.selectNoteById(noteId);

        // 笔记判空
        if(Objects.isNull(noteDO)) {
            throw new BizException(ResponseCodeEnum.NOTE_NOT_FOUND);
        }

        // 笔记操作权限校验
        if(!Objects.equals(userId, noteDO.getCreatorId())) {
            throw new BizException(ResponseCodeEnum.NOTE_CANT_OPERATE);
        }
    }

    private void checkNoteExist(Long noteId) {
        String findNoteDetailRspVOStrLocalCache = LOCAL_CACHE.getIfPresent(noteId);
        FindNoteDetailRspVO findNoteDetailRspVO = JsonUtils.parseObject(findNoteDetailRspVOStrLocalCache, FindNoteDetailRspVO.class);
        if(Objects.isNull(findNoteDetailRspVO)) {
            String noteDetailJson = (String) redisTemplate.opsForValue().get(RedisKeyConstants.buildNoteDetailKey(noteId));
            findNoteDetailRspVO = JsonUtils.parseObject(noteDetailJson, FindNoteDetailRspVO.class);

            if(Objects.isNull(findNoteDetailRspVO)) {
                NoteDO note = noteDOMapper.selectNoteById(noteId);
                if(Objects.isNull(note)) throw new BizException(ResponseCodeEnum.NOTE_NOT_FOUND);
            }

            threadPoolTaskExecutor.execute(() -> {
                FindNoteDetailReqVO findNoteDetailReqVO = FindNoteDetailReqVO.builder().id(noteId).build();
                findNoteDetail(findNoteDetailReqVO);
            });
        }
    }

    /**
     * 异步更新布隆过滤器
     * @param userId
     * @param expireTime
     * @param redisKey
     */
    private void asynBatchAddNoteLikeAndExpire(Long userId, long expireTime, String redisKey) {
        threadPoolTaskExecutor.execute(() -> {
            List<NoteLikeDO> noteLikeDOS = noteLikeDOMapper.selectByUserId(userId);
            if(CollUtil.isNotEmpty(noteLikeDOS)) {
                DefaultRedisScript<Long> script = new DefaultRedisScript<>();
                script.setResultType(Long.class);
                script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/batch_bloom_add_note_like_and_expire.lua")));

                List<Object> luaArgs = new ArrayList<>();
                noteLikeDOS.forEach(noteLikeDO -> {
                    luaArgs.add(noteLikeDO.getNoteId());
                });
                luaArgs.add(expireTime);
                redisTemplate.execute(script, Collections.singletonList(redisKey), luaArgs);
            }
        });
    }

    private static int getRandomExpireTime() {
        return 60*60*24 + RandomUtil.randomInt(60*60*24);
    }

    private Object[] buildNoteLikeZsetLuaArgs(List<NoteLikeDO> noteLikeDOS, long expireTime) {
        int n = noteLikeDOS.size();
        Object[] args = new Object[n * 2 + 1];
        int i = 0;
        for(NoteLikeDO noteLikeDO: noteLikeDOS) {
            args[i] = DateUtils.localDateTime2Timestamp(noteLikeDO.getCreateTime());
            args[i + 1] = noteLikeDO.getNoteId();
            i += 2;
        }
        args[n] = expireTime;
        return args;
    }

    private void asynInitUserNoteLikesZset(Long userId, String noteLikeRedisKey) {
        threadPoolTaskExecutor.execute(() -> {
            boolean hasKey = redisTemplate.hasKey(noteLikeRedisKey);
            if(!hasKey) {
                List<NoteLikeDO> noteLikeDOS = noteLikeDOMapper.selectByUserIdAndLimit(userId, 100);
                if(CollUtil.isNotEmpty(noteLikeDOS)) {
                    Object[] luaArgs = buildNoteLikeZsetLuaArgs(noteLikeDOS, getRandomExpireTime());
                    DefaultRedisScript<Long> script2 = new DefaultRedisScript<>();
                    script2.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/batch_add_note_like_zset_and_expire.lua")));
                    script2.setResultType(Long.class);
                    redisTemplate.execute(script2, Collections.singletonList(noteLikeRedisKey), luaArgs);

                }
            }
        });
    }
}
