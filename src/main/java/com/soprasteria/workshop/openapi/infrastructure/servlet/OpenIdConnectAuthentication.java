package com.soprasteria.workshop.openapi.infrastructure.servlet;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.actioncontroller.ExceptionUtil;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.UserIdentity;
import org.jsonbuddy.JsonObject;
import org.jsonbuddy.parse.JsonHttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;

public class OpenIdConnectAuthentication implements Authentication.Deferred {

    private static final Logger logger = LoggerFactory.getLogger(OpenIdConnectAuthentication.class);
    
    @Override
    public Authentication authenticate(ServletRequest request) {
        Request req = (Request) request;
        String authorization = req.getHeader("Authorization");
        if (authorization == null) {
            return null;
        }
        return createUserAuthentication(getAuthorization(authorization));
    }

    private OpenIdConnectUserPrincipal getAuthorization(String authorization) {
        try {
            JsonObject configuration = JsonObject.read(new URL("https://login.microsoftonline.com/common/.well-known/openid-configuration"));
            URL userinfoEndpoint = new URL(configuration.requiredString("userinfo_endpoint"));

            HttpURLConnection connection = (HttpURLConnection) userinfoEndpoint.openConnection();
            connection.setRequestProperty("Authorization", authorization);
            return new OpenIdConnectUserPrincipal(JsonObject.read(connection));
        } catch (JsonHttpException e) {
            logger.error("Error reading from authentication provider", e);
            return null;
        } catch (IOException e) {
            throw ExceptionUtil.softenException(e);
        }
    }

    private UserAuthentication createUserAuthentication(OpenIdConnectUserPrincipal userPrincipal) {
        return new UserAuthentication("active directory", createUserIdentity(userPrincipal, userPrincipal.getRoles()));
    }

    private UserIdentity createUserIdentity(Principal principal, String[] roles) {
        Subject subject = new Subject();
        subject.getPrincipals().add(principal);
        return new DefaultUserIdentity(subject, principal, roles);
    }

    @Override
    public Authentication authenticate(ServletRequest request, ServletResponse response) {
        return null;
    }

    @Override
    public Authentication login(String username, Object password, ServletRequest request) {
        return null;
    }

    @Override
    public Authentication logout(ServletRequest request) {
        return null;
    }
}
