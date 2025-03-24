package org.n1vnhil.xhs.kv.biz.service;


import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.kv.biz.domain.dataobject.NoteContentDO;
import org.n1vnhil.xhs.kv.dto.req.AddNoteContentReqDTO;
import org.n1vnhil.xhs.kv.dto.req.DeleteNoteContentDTO;
import org.n1vnhil.xhs.kv.dto.req.FindNoteContentReqDTO;
import org.n1vnhil.xhs.kv.dto.rsp.FindNoteContentRspDTO;

public interface NoteContentService {

    Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO);

    Response<FindNoteContentRspDTO> findNoteContent(FindNoteContentReqDTO findNoteContentReqDTO);

    Response<?> deleteNoteContent(DeleteNoteContentDTO deleteNoteContentDTO);
}
