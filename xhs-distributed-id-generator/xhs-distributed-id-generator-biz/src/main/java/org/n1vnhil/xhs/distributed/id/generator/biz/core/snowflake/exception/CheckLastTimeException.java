package org.n1vnhil.xhs.distributed.id.generator.biz.core.snowflake.exception;

public class CheckLastTimeException extends RuntimeException {
    public CheckLastTimeException(String msg){
        super(msg);
    }
}
