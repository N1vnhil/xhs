package org.n1vnhil.xhsauth.alarm;

public interface AlarmInterface {

    /**
     * 发送告警信息
     * @param message 告警信息
     * @return
     */
    boolean send(String message);

}
