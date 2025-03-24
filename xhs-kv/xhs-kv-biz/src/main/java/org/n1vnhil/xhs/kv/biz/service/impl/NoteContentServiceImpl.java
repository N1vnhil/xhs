package org.n1vnhil.xhs.kv.biz.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.kv.biz.domain.dataobject.NoteContentDO;
import org.n1vnhil.xhs.kv.biz.domain.repository.NoteContentRepository;
import org.n1vnhil.xhs.kv.biz.service.NoteContentService;
import org.n1vnhil.xhs.kv.dto.req.AddNoteContentReqDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class NoteContentServiceImpl implements NoteContentService {

    @Resource
    private NoteContentRepository noteContentRepository;

    @Override
    public Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO) {
        log.info("==========> 笔记写入Cassandra：{}", addNoteContentReqDTO);
        Long noteId = addNoteContentReqDTO.getNoteId();
        String content = addNoteContentReqDTO.getContent();

        NoteContentDO noteContentDO = NoteContentDO.builder()
                .content(content)
                .id(UUID.randomUUID())
                .build();

        noteContentRepository.save(noteContentDO);

        return Response.success();
    }
}
