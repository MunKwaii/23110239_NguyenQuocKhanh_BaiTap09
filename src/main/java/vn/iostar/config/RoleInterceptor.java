package vn.iostar.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.iostar.entity.Role;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String uri = req.getRequestURI();
        HttpSession session = req.getSession(false);
        Role role = (session == null) ? null : (Role) session.getAttribute("ROLE");

        if (uri.startsWith("/user") || uri.startsWith("/admin")) {
            if (role == null) {
                res.sendRedirect("/login");
                return false;
            }
        }

        if (uri.startsWith("/admin") && role != Role.ADMIN) {
            res.sendRedirect("/login");
            return false;
        }
        if (uri.startsWith("/user") && role != Role.USER) {
            res.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
