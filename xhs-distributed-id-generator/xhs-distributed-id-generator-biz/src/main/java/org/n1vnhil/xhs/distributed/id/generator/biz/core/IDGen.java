package org.n1vnhil.xhs.distributed.id.generator.biz.core;

import org.n1vnhil.xhs.distributed.id.generator.biz.core.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
