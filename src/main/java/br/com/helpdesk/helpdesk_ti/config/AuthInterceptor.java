package br.com.helpdesk.helpdesk_ti.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.*;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {
        if(request.getSession(false)!=null && request.getSession(false).getAttribute("usuario")!=null) return true;
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Não autenticado"); return false;
    }
}
