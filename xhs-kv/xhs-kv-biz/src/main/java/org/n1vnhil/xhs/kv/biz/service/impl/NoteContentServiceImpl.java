package org.n1vnhil.xhs.kv.biz.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.kv.biz.domain.dataobject.NoteContentDO;
import org.n1vnhil.xhs.kv.biz.domain.repository.NoteContentRepository;
import org.n1vnhil.xhs.kv.biz.enums.ResponseCodeEnum;
import org.n1vnhil.xhs.kv.biz.service.NoteContentService;
import org.n1vnhil.xhs.kv.dto.req.AddNoteContentReqDTO;
import org.n1vnhil.xhs.kv.dto.req.DeleteNoteContentDTO;
import org.n1vnhil.xhs.kv.dto.req.FindNoteContentReqDTO;
import org.n1vnhil.xhs.kv.dto.rsp.FindNoteContentRspDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class NoteContentServiceImpl implements NoteContentService {

    @Resource
    private NoteContentRepository noteContentRepository;

    @Override
    public Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO) {
        log.info("==========> 笔记写入Cassandra：{}", addNoteContentReqDTO);
        String noteId = addNoteContentReqDTO.getUuid();
        String content = addNoteContentReqDTO.getContent();

        NoteContentDO noteContentDO = NoteContentDO.builder()
                .content(content)
                .id(UUID.randomUUID())
                .build();

        noteContentRepository.save(noteContentDO);

        return Response.success();
    }

    @Override
    public Response<FindNoteContentRspDTO> findNoteContent(FindNoteContentReqDTO findNoteContentReqDTO) {
        log.info("==========> 查询笔记：{}", findNoteContentReqDTO);
        String noteId = findNoteContentReqDTO.getUuid();
        Optional<NoteContentDO> optional = noteContentRepository.findById(UUID.fromString(noteId));
        if(!optional.isPresent()) throw new BizException(ResponseCodeEnum.NOTE_CONTENT_NOT_FOUND);

        NoteContentDO noteContentDO = optional.get();
        FindNoteContentRspDTO findNoteContentRspDTO = FindNoteContentRspDTO.builder()
                .content(noteContentDO.getContent())
                .noteId(noteContentDO.getId())
                .build();

        return Response.success(findNoteContentRspDTO);
    }

    @Override
    public Response<?> deleteNoteContent(DeleteNoteContentDTO deleteNoteContentDTO) {
        log.info("==========> 删除笔记：{}", deleteNoteContentDTO);
        UUID uuid = UUID.fromString(deleteNoteContentDTO.getUuid());
        noteContentRepository.deleteById(uuid);
        return Response.success();
    }
}
