package com.hotelbooking.hotelbookingapp.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
// @Slf4j // Lombok annotation removed
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // Manual Logger initialization
    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.warn("Redirecting an authenticated user to the role-designated landing page");

        String redirectUrl = RedirectUtil.getRedirectUrl(authentication);

        log.info("Redirect path: " + redirectUrl);

        if (redirectUrl == null) {
            log.error("Redirect URL is null, cannot determine user role for redirection. Authentication: {}", authentication.getName());
            throw new IllegalStateException("Could not determine user role for redirection");
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}