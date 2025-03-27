package org.n1vnhil.xhs.note.biz.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.biz.operationlog.aspect.ApiOperationLog;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.note.biz.model.vo.*;
import org.n1vnhil.xhs.note.biz.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/note")
@Slf4j
public class NoteController {

    @Autowired
    private NoteService noteService;

    @ApiOperationLog(description = "发布笔记")
    @PostMapping("/publish")
    public Response<?> publishNote(@Validated @RequestBody PublishNoteReqVO publishNoteReqVO) {
        return noteService.publishNote(publishNoteReqVO);
    }

    @ApiOperationLog(description = "获取笔记详情")
    @PostMapping("/detail")
    public Response<FindNoteDetailRspVO> getNoteDetail(@Validated @RequestBody FindNoteDetailReqVO findNoteDetailReqVO) {
        return noteService.findNoteDetail(findNoteDetailReqVO);
    }

    @ApiOperationLog(description = "更新笔记")
    @PostMapping("/update")
    public Response<?> updateNoteReqVOResponse(@Validated @RequestBody UpdateNoteReqVO updateNoteReqVO) {
        return noteService.updateNote(updateNoteReqVO);
    }

    @ApiOperationLog(description = "删除笔记")
    @PostMapping("/delete")
    public Response<?> deleteNote(@Validated @RequestBody DeleteNoteReqVO deleteNoteReqVO) {
        return noteService.deleteNote(deleteNoteReqVO);
    }

    @ApiOperationLog(description = "设置仅自己可见")
    @PostMapping("/visible/onlyme")
    public Response<?> setOnlyMe(@Validated @RequestBody OnlyMeVisibleReqVO onlyMeVisibleReqVO) {
        return noteService.setOnlyMe(onlyMeVisibleReqVO);
    }

    @ApiOperationLog(description = "笔记置顶/取消置顶")
    @PostMapping("/top")
    public Response<?> top(@Validated @RequestBody TopNoteReqVO topNoteReqVO) {
        return noteService.setTopStatus(topNoteReqVO);
    }

}
