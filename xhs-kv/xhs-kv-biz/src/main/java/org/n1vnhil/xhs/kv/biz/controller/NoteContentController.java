package org.n1vnhil.xhs.kv.biz.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.kv.biz.service.NoteContentService;
import org.n1vnhil.xhs.kv.dto.req.AddNoteContentReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/note")
public class NoteContentController {

    @Autowired
    private NoteContentService noteContentService;

    @PostMapping("/content/add")
    public Response<?> addNote(@RequestBody @Validated AddNoteContentReqDTO addNoteContentReqDTO) {
        return noteContentService.addNoteContent(addNoteContentReqDTO);
    }

}
