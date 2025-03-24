package org.n1vnhil.xhs.kv.api;

import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.xhs.kv.constant.ApiConstants;
import org.n1vnhil.xhs.kv.dto.req.AddNoteContentReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = ApiConstants.SERVICE_NAME)
public interface KvFeignApi {

    String PREFIX = "/kv";

    @PostMapping(value = PREFIX + "/note/content/add")
    Response<?> addNoteContent(@RequestBody AddNoteContentReqDTO addNoteContentReqDTO);

}
