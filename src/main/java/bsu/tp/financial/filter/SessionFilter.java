package bsu.tp.financial.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession(false);
        if(session == null || (session.getAttribute("admin") == null && session.getAttribute("user") == null)){
            resp.sendRedirect(req.getContextPath() + "/signIn");
            return;
        }
        filterChain.doFilter(req, resp);
    }
}
