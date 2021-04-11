import * as React from "react";
import { OpenIdConnectProvider } from "../applicationContext";
import { randomString } from "../lib/randomString";
import { fetchJson } from "../lib/fetchJson";
import { sha256 } from "../lib/sha256";
import { LoadingView } from "../views/LoadingView";
import { useEffect } from "react";

export function LoginStartPage({
  provider,
}: {
  provider: OpenIdConnectProvider;
}) {
  const { openIdConnectUrl, client_id, domain_hint } = provider;

  async function startLogin() {
    const code_verifier = randomString(50);
    const state = randomString(30);
    sessionStorage.setItem(
      "loginState",
      JSON.stringify({ code_verifier, state })
    );

    const { authorization_endpoint } = await fetchJson(openIdConnectUrl);
    const payload = {
      response_type: "code",
      response_mode: "fragment",
      client_id,
      state,
      scope: "openid email profile",
      code_challenge: await sha256(code_verifier),
      code_challenge_method: "S256",
      redirect_uri: window.location.origin + "/login/callback",
      ...(domain_hint ? { domain_hint } : {}),
    };

    window.location.href =
      authorization_endpoint + "?" + new URLSearchParams(payload);
  }

  useEffect(() => {
    startLogin();
  }, []);

  return <LoadingView />;
}
