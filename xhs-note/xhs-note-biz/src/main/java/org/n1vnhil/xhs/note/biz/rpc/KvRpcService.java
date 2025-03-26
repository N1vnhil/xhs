package org.n1vnhil.xhs.note.biz.rpc;

import jakarta.annotation.Resource;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.kv.api.KvFeignApi;
import org.n1vnhil.xhs.kv.dto.req.AddNoteContentReqDTO;
import org.n1vnhil.xhs.kv.dto.req.DeleteNoteContentDTO;
import org.n1vnhil.xhs.kv.dto.req.FindNoteContentReqDTO;
import org.n1vnhil.xhs.kv.dto.rsp.FindNoteContentRspDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class KvRpcService {

    @Resource
    private KvFeignApi kvFeignApi;

    /**
     * 保存笔记
     * @param uuid
     * @param content
     * @return
     */
    public boolean saveNoteContent(String uuid, String content) {
        AddNoteContentReqDTO addNoteContentReqDTO = AddNoteContentReqDTO.builder()
                .content(content)
                .uuid(uuid)
                .build();
        Response<?> response = kvFeignApi.addNoteContent(addNoteContentReqDTO);

        return Objects.nonNull(response) && response.isSuccess();
    }

    /**
     * 删除笔记
     * @param uuid
     * @return
     */
    public boolean deleteNoteContent(String uuid) {
        DeleteNoteContentDTO deleteNoteContentDTO = DeleteNoteContentDTO.builder()
                .uuid(uuid)
                .build();
        Response<?> response = kvFeignApi.deleteNoteContent(deleteNoteContentDTO);
        return Objects.nonNull(response) && response.isSuccess();
    }

    public String getNoteContent(String uuid) {
        FindNoteContentReqDTO findNoteContentReqDTO = FindNoteContentReqDTO.builder()
                .uuid(uuid)
                .build();
        Response<FindNoteContentRspDTO> response = kvFeignApi.findNoteContent(findNoteContentReqDTO);
        if(Objects.isNull(response) || !response.isSuccess() || Objects.isNull(response.getData())) return null;
        return response.getData().getContent();
    }

}
