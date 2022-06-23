package com.soprasteria.workshop.infrastructure.servlet;

import org.jsonbuddy.JsonObject;

import java.security.Principal;

public class OpenIdConnectUserPrincipal implements Principal, PetStoreUser {
    private final JsonObject userinfo;
    private final String username;

    public OpenIdConnectUserPrincipal(JsonObject userinfo) {
        this.userinfo = userinfo;
        this.username = userinfo.stringValue("unique_name")
                .orElseGet(() -> userinfo.requiredString("sub"));
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String getFirstName() {
        return userinfo.stringValue("given_name").orElse(null);
    }

    @Override
    public String getLastName() {
        return userinfo.stringValue("family_name").orElse(null);
    }

    @Override
    public String getEmail() {
        return userinfo.stringValue("email")
                .or(() -> userinfo.stringValue("unique_name"))
                .orElse(null);
    }

    public String[] getRoles() {
        return userinfo.arrayValue("roles")
                .map(a -> a.strings().toArray(new String[0]))
                .orElse(null);
    }
}
