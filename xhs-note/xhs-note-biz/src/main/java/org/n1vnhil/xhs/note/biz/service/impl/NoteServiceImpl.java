package org.n1vnhil.xhs.note.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n1vnhil.framework.common.enums.StatusEnum;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;
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
import org.n1vnhil.xhs.note.biz.rpc.DistributedIdGeneratorRpcService;
import org.n1vnhil.xhs.note.biz.rpc.KvRpcService;
import org.n1vnhil.xhs.note.biz.rpc.UserRpcService;
import org.n1vnhil.xhs.note.biz.service.NoteService;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class NoteServiceImpl implements NoteService {

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
        NoteDO noteDO = noteDOMapper.selectNoteById(noteId);

        if(Objects.isNull(noteDO)) {
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

        return Response.success(findNoteDetailRspVO);
    }

    private void checkNoteVisible(Integer visible, Long userId, Long creatorId) {
        if(Objects.equals(visible, NoteVisibleEnum.PRIVATE.getCode())
                && !Objects.equals(userId, creatorId)) {
            throw new BizException(ResponseCodeEnum.NOTE_PRIVATE);
        }
    }
}
