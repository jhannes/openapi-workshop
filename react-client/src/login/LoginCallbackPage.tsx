import * as React from "react";
import { useEffect } from "react";
import { OpenIdConnectProvider } from "../applicationContext";
import { fetchJson } from "../lib/fetchJson";
import { Link } from "react-router-dom";
import { LoadingView } from "../views/LoadingView";

export interface TokenResponse {
  access_token: string;
}

export function LoginCallbackPage({
  provider,
  onComplete,
}: {
  provider: OpenIdConnectProvider;
  onComplete(tokenResponse: TokenResponse): void;
}) {
  const { openIdConnectUrl, client_id } = provider;
  const hash = Object.fromEntries(
    new URLSearchParams(window.location.hash.substr(1))
  );
  const { error, error_description, state, code } = hash;
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const loginState = JSON.parse(sessionStorage.getItem("loginState")!);

  async function fetchToken(code: string, code_verifier: string) {
    const { token_endpoint } = await fetchJson(openIdConnectUrl);
    const body = {
      grant_type: "authorization_code",
      code,
      client_id,
      redirect_uri: window.location.origin + "/login/callback",
      code_verifier,
    };
    const tokenResponse: TokenResponse = await fetchJson(token_endpoint, {
      method: "POST",
      body: new URLSearchParams(body),
    });
    console.log(tokenResponse);
    sessionStorage.removeItem("loginState");
    onComplete(tokenResponse);
  }

  useEffect(() => {
    (async () => {
      if (code && loginState) {
        const { code_verifier } = loginState;
        await fetchToken(code, code_verifier);
      }
    })();
  }, [code]);

  if (!state || state != loginState?.state) {
    return <div>Error: Request not initiated from this website</div>;
  }

  if (error) {
    return (
      <div>
        <h1>{error}</h1>
        {error_description && <div>{error_description}</div>}
        <Link to={"/login"}>
          <button>Try again</button>
        </Link>
      </div>
    );
  }

  return <LoadingView />;
}
