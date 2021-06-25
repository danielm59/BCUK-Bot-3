package com.expiredminotaur.bcukbot.web.security;

import com.expiredminotaur.bcukbot.web.view.login.LoginView;
import com.expiredminotaur.bcukbot.web.view.login.UnauthorisedView;
import com.expiredminotaur.bcukbot.web.view.stream.StreamView;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils
{

    public static boolean isAccessGranted(Class<?> securedClass, UserTools userTools)
    {
        final boolean publicView = LoginView.class.equals(securedClass)
                || UnauthorisedView.class.equals(securedClass)
                || StreamView.class.equals(securedClass);

        // Always allow access to public views
        if (publicView)
        {
            return true;
        }

        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();

        // All other views require authentication
        if (!isUserLoggedIn(userAuthentication))
        {
            return false;
        }

        // Allow if no roles are required.
        AccessLevel accessLevel = AnnotationUtils.findAnnotation(securedClass, AccessLevel.class);
        if (accessLevel == null)
        {
            return true;
        }

        return userTools.hasAccess(accessLevel.value());
    }

    public static boolean isUserLoggedIn()
    {
        return isUserLoggedIn(SecurityContextHolder.getContext().getAuthentication());
    }

    private static boolean isUserLoggedIn(Authentication authentication)
    {
        return authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
