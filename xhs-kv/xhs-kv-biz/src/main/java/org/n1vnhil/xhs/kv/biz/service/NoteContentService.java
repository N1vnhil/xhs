package org.n1vnhil.xhs.kv.biz.service;


import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.kv.dto.req.AddNoteContentReqDTO;

public interface NoteContentService {

    Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO);

}
