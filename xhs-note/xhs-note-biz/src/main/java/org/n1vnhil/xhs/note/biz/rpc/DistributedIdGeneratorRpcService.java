package org.n1vnhil.xhs.note.biz.rpc;

import jakarta.annotation.Resource;
import org.n1vnhil.xhs.distributed.id.generator.api.DistributedIdGeneratorFeignApi;
import org.springframework.stereotype.Component;

@Component
public class DistributedIdGeneratorRpcService {

    @Resource
    private DistributedIdGeneratorFeignApi distributedIdGeneratorFeignApi;

    /**
     * 生成雪花算法id
     * @return
     */
    public String getSnowflakeId() {
        return distributedIdGeneratorFeignApi.getSnowflakeId("test");
    }

}
