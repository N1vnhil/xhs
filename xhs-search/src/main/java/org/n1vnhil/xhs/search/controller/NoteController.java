package org.n1vnhil.xhs.search.controller;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.xhs.search.model.vo.SearchNoteReqVO;
import org.n1vnhil.xhs.search.model.vo.SearchNoteRspVO;
import org.n1vnhil.xhs.search.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/search")
public class NoteController {

    @Autowired
    private NoteService noteService;

    public PageResponse<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO) {
        return noteService.searchNote(searchNoteReqVO);
    }

}
