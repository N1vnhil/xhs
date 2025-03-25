package org.n1vnhil.xhs.user.biz.rpc;

import org.n1vnhil.xhs.distributed.id.generator.api.DistributedIdGeneratorFeignApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistributedIdGeneratorRpcService {

    @Autowired
    DistributedIdGeneratorFeignApi distributedIdGeneratorFeignApi;

    private static final String BIZ_TAG_XHS_ID = "leaf-segment-xhs-id";

    private static final String BIZ_TAG_USER_ID = "leaf-segment-user-id";

    public String getXhsId() {
        return distributedIdGeneratorFeignApi.getSegmentId(BIZ_TAG_XHS_ID);
    }

    public String getUserId() {
        return distributedIdGeneratorFeignApi.getSegmentId(BIZ_TAG_USER_ID);
    }

}
