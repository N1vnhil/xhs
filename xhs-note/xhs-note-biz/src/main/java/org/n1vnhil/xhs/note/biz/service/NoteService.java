package org.n1vnhil.xhs.note.biz.service;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.note.biz.model.vo.*;

public interface NoteService {

    /**
     * 笔记发布
     * @param publishNoteReqVO
     * @return
     */
    Response<?> publishNote(PublishNoteReqVO publishNoteReqVO);

    /**
     * 获取笔记详情
     * @param findNoteDetailReqVO
     * @return
     */
    Response<FindNoteDetailRspVO> findNoteDetail(FindNoteDetailReqVO findNoteDetailReqVO);

    /**
     * 更新笔记
     * @param updateNoteReqVO
     * @return
     */
    Response<?> updateNote(UpdateNoteReqVO updateNoteReqVO);

    /**
     * 删除本地缓存
     * @param noteId
     */
    void deleteLocalNoteCache(Long noteId);

    /**
     * 删除笔记
     * @param deleteNoteReqVO
     * @return
     */
    Response<?> deleteNote(DeleteNoteReqVO deleteNoteReqVO);

    /**
     * 笔记设置为仅自己可见
     * @param onlyMeVisibleReqVO
     * @return
     */
    Response<?> setOnlyMe(OnlyMeVisibleReqVO onlyMeVisibleReqVO);
}
