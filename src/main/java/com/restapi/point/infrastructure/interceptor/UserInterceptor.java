package com.restapi.point.infrastructure.interceptor;

import com.restapi.point.application.enums.Messages;
import com.restapi.point.application.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String[] uriParts = requestURI.split("/");

        // 사용자 id 가 누락된 경우
        if(uriParts.length <= 1) {
            throw new BusinessException(400 , Messages.BAD_REQUEST);
        }

        try {
            // 사용자 id 가 숫자가 아닌 경우
            // 사용자 id가 long범위를 넘어서는 경우
            long userId = Long.parseLong(uriParts[2]);
        } catch(Exception e) {
            throw new BusinessException(400 , Messages.BAD_REQUEST);
        }

        return true;
    }
}
