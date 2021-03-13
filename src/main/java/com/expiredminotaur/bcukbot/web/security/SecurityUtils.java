package com.expiredminotaur.bcukbot.web.security;

import com.expiredminotaur.bcukbot.web.view.login.LoginView;
import com.expiredminotaur.bcukbot.web.view.login.UnauthorisedView;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public class SecurityUtils
{

    public static boolean isAccessGranted(Class<?> securedClass, UserTools userTools)
    {
        final boolean publicView = LoginView.class.equals(securedClass)
                || UnauthorisedView.class.equals(securedClass);

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
        Secured secured = AnnotationUtils.findAnnotation(securedClass, Secured.class);
        if (secured == null)
        {
            return true;
        }

        List<String> allowedRoles = Arrays.asList(secured.value());
        if (allowedRoles.contains("ADMIN") && userTools.isCurrentUserAdmin())
            return true;
        if (allowedRoles.contains("MOD") && userTools.isCurrentUserMod())
            return true;
        return false;
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
