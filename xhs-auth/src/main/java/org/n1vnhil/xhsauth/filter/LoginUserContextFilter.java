package org.n1vnhil.xhsauth.filter;

import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.constant.GlobalConstants;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@Component
@Slf4j
public class LoginUserContextFilter {

    /**
     *  用户id ThreadLocal
     */
    private static final ThreadLocal<Map<String, Object>> USER_LOGIN_CONTEXT_THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);

    public static Long getLoginUserId() {
        Object o = USER_LOGIN_CONTEXT_THREAD_LOCAL.get().get(GlobalConstants.USER_ID);
        return Objects.isNull(o) ? null : Long.valueOf(o.toString());
    }

    public static void setLoginUserId(Long loginUserId) {
        USER_LOGIN_CONTEXT_THREAD_LOCAL.get().put(GlobalConstants.USER_ID, loginUserId);
    }

    public static void remove() {
        USER_LOGIN_CONTEXT_THREAD_LOCAL.remove();
    }

}
