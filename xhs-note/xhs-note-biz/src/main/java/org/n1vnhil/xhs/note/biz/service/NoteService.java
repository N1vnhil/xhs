package org.n1vnhil.xhs.note.biz.service;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.note.biz.model.vo.PublishNoteReqVO;

public interface NoteService {

    /**
     * 笔记发布
     * @param publishNoteReqVO
     * @return
     */
    Response<?> publishNote(PublishNoteReqVO publishNoteReqVO);

}
