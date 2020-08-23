package com.expiredminotaur.bcukbot.web.security;

import com.expiredminotaur.bcukbot.sql.user.User;
import com.expiredminotaur.bcukbot.sql.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserTools
{
    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Autowired
    private UserRepository users;

    public String getCurrentUsersName()
    {
        return getPrincipalAttribute("username");
    }

    public Long getCurrentUsersID()
    {
        return Long.parseLong(getPrincipalAttribute("id"));
    }

    public boolean isCurrentUserAdmin()
    {
        return users.findById(getCurrentUsersID()).map(User::isAdmin).orElse(false);
    }

    public String getCurrentUsersToken()
    {
        OAuth2AuthenticationToken oauthToken = getAuthentication();
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
        return client.getAccessToken().getTokenValue();
    }

    public OAuth2AuthenticationToken getAuthentication()
    {
        return (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    private String getPrincipalAttribute(String key)
    {
        String error = "ERROR please report to the Admin";
        OAuth2AuthenticationToken auth = getAuthentication();
        if (auth != null)
        {
            Object principal = auth.getPrincipal();
            if (principal instanceof DefaultOAuth2User)
            {
                return (String) ((DefaultOAuth2User) principal).getAttributes().get(key);
            }
        }
        return error;
    }
}
