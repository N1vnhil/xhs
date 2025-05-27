package org.n1vnhil.xhs.search.service;

import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.xhs.search.model.vo.SearchNoteReqVO;
import org.n1vnhil.xhs.search.model.vo.SearchNoteRspVO;

public interface NoteService {

    PageResponse<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO);

}
