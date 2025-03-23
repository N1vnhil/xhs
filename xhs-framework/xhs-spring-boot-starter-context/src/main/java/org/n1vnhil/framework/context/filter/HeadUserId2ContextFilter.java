package org.n1vnhil.framework.context.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.constant.GlobalConstants;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class HeadUserId2ContextFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String loginId = request.getHeader(GlobalConstants.USER_ID);
        log.info("===========> HeaderUserId2Context, loginId:{}", loginId);

        if(StringUtils.isBlank(loginId)) {
            filterChain.doFilter(request, response);
        }

        else {
            LoginUserContextHolder.setLoginUserId(Long.valueOf(loginId));
            try {
                filterChain.doFilter(request, response);
            } finally {
                LoginUserContextHolder.remove();
            }
        }

    }

}
