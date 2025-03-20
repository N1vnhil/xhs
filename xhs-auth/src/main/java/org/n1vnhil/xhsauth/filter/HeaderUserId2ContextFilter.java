package org.n1vnhil.xhsauth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n1vnhil.framework.common.constant.GlobalConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class HeaderUserId2ContextFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String loginId = request.getHeader(GlobalConstants.USER_ID);
        log.info("===========> HeaderUserId2Context, loginId:{}", loginId);

        if(StringUtils.isBlank(loginId)) {
            filterChain.doFilter(request, response);
        }

        else {
            LoginUserContextFilter.setLoginUserId(Long.valueOf(loginId));
            try {
                filterChain.doFilter(request, response);
            } finally {
                LoginUserContextFilter.remove();
            }
        }

    }

}
