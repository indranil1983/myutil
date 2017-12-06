package com.ericsson.spring.security;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ericsson.spring.constants.EBIToolConstants;
import com.ericsson.spring.model.UserSession;
 
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
 
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
 
    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        HttpSession currSession = request.getSession();
        String sessionId = currSession.getId();
        UserSession userSess = new UserSession();
        userSess.setSessionId(sessionId);
        userSess.setLoginTime(Calendar.getInstance().getTimeInMillis());
        if(authentication!=null){
        	userSess.setUserId(authentication.getName());
        }
        currSession.setAttribute(EBIToolConstants.USERSESSION_CONSTANTS.USER_SESSION_STRING, userSess);
        if (response.isCommitted()) {
            System.out.println("Can't redirect");
            return;
        }
 
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
 
    /*
     * This method extracts the roles of currently logged-in user and returns
     * appropriate URL according to his/her role.
     */
    protected String determineTargetUrl(Authentication authentication) {
        String url = "";
 
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
 
        List<String> roles = new ArrayList<String>();
 
        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
 
        if (isProd(roles)) {
            url = "/production";
        } else if (isAdmin(roles)) {
            url = "/lab";
        } else if (isLab(roles)) {
            url = "/lab";
        } else {
            url = "/accessDenied";
        }
 
        return url;
    }
 
    private boolean isLab(List<String> roles) {
        if (roles.contains("EBI_DEV")) {
            return true;
        }
        return false;
    }
 
    private boolean isAdmin(List<String> roles) {
        if (roles.contains("EBI_ADMIN")) {
            return true;
        }
        return false;
    }
 
    private boolean isProd(List<String> roles) {
        if (roles.contains("EBI_OPS")) {
            return true;
        }
        return false;
    }
 
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
 
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }
 
}