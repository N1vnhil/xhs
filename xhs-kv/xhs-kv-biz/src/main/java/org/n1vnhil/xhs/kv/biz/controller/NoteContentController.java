package org.n1vnhil.xhs.kv.biz.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.kv.biz.service.NoteContentService;
import org.n1vnhil.xhs.kv.dto.req.AddNoteContentReqDTO;
import org.n1vnhil.xhs.kv.dto.req.DeleteNoteContentDTO;
import org.n1vnhil.xhs.kv.dto.req.FindNoteContentReqDTO;
import org.n1vnhil.xhs.kv.dto.rsp.FindNoteContentRspDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/kv")
public class NoteContentController {

    @Autowired
    private NoteContentService noteContentService;

    @PostMapping("/note/content/add")
    public Response<?> addNote(@RequestBody @Validated AddNoteContentReqDTO addNoteContentReqDTO) {
        return noteContentService.addNoteContent(addNoteContentReqDTO);
    }

    @PostMapping("/note/content/find")
    public Response<FindNoteContentRspDTO> findNote(@RequestBody @Validated FindNoteContentReqDTO findNoteContentReqDTO) {
        return noteContentService.findNoteContent(findNoteContentReqDTO);
    }

    @PostMapping("/note/content/delete")
    public Response<?> deleteNote(@RequestBody @Validated DeleteNoteContentDTO deleteNoteContentDTO) {
        return noteContentService.deleteNoteContent(deleteNoteContentDTO);
    }

}
