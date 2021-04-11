import { OpenIdConnectProvider } from "../applicationContext";
import { LoginCallbackPage, TokenResponse } from "./LoginCallbackPage";
import { Route, Switch } from "react-router";
import React from "react";
import { LoginStartPage } from "./LoginStartPage";

export function LoginPage({
  onComplete,
  provider,
}: {
  provider: OpenIdConnectProvider;
  onComplete: (tokenResponse: TokenResponse) => void;
}) {
  return (
    <Switch>
      <Route path={"/login/callback"}>
        <LoginCallbackPage provider={provider} onComplete={onComplete} />
      </Route>
      <Route path={"/login/authorize"} exact>
        <LoginStartPage provider={provider} />
      </Route>
      <Route>
        <h1>Not found</h1>
      </Route>
    </Switch>
  );
}
