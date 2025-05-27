package org.n1vnhil.xhs.search.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.xhs.search.model.vo.SearchNoteReqVO;
import org.n1vnhil.xhs.search.model.vo.SearchNoteRspVO;
import org.n1vnhil.xhs.search.service.NoteService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    @Override
    public PageResponse<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO) {
        return null;
    }
}
