package org.n1vnhil.xhsauth.alarm.impl;

import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.xhsauth.alarm.AlarmInterface;

@Slf4j
public class SmsAlarmHelper implements AlarmInterface {

    @Override
    public boolean send(String message) {
        log.info("============> 【短信告警】：{}", message);

        return true;
    }

}
