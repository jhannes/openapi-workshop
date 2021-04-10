import * as React from "react";
import { useContext, useEffect, useState } from "react";
import * as ReactDOM from "react-dom";
import {
  ApplicationApis,
  PetApi,
  petstore_auth,
  servers,
  StoreApi,
  UserApi,
} from "@jhannes/openapi-workshop";
import { BrowserRouter, Link } from "react-router-dom";
import { Route, Switch, useHistory } from "react-router";
import { HttpError } from "@jhannes/openapi-workshop/dist/base";

type LoadingState<T> =
  | { state: "loading" }
  | { state: "complete"; data: T }
  | { state: "error"; error: Error };

function useLoader<T>(loadingFunction: () => Promise<T>): LoadingState<T> {
  const [state, setState] = useState<LoadingState<T>>({ state: "loading" });

  useEffect(() => {
    (async () => {
      setState({ state: "loading" });
      try {
        setState({ state: "complete", data: await loadingFunction() });
      } catch (error) {
        console.warn(error);
        setState({ state: "error", error });
      }
    })();
  }, []);

  return state;
}

function AddPet() {
  const petApi = new PetApi();
  const [category, setCategory] = useState();
  const [name, setName] = useState();
  const [status, setStatus] = useState();

  const security = new petstore_auth("test");

  function handleSubmit() {
    petApi.addPet({
      petDto: { category, name, status },
      security,
    });
  }
}

const ApiContext = React.createContext<{
  apis: ApplicationApis;
  activeDirectory: {
    openIdConnectUrl: string;
    client_id: string;
    domain_hint?: string;
  };
}>({
  apis: servers.current,
  activeDirectory: {
    openIdConnectUrl:
      "https://login.microsoftonline.com/common/.well-known/openid-configuration",
    client_id: "",
  },
});

function ListCategories() {
  const {
    apis: { petApi },
  } = useContext(ApiContext);

  const state = useLoader(async () => await petApi.listCategories());

  if (state.state === "loading") {
    return <div>Loading...</div>;
  }
  if (state.state === "error") {
    return <div>Error: {state.error.toString()}</div>;
  }
  return (
    <ul>
      {state.data.map(({ id, name }) => (
        <li key={id}>{name}</li>
      ))}
    </ul>
  );
}

async function fetchJson(url: string, options?: RequestInit) {
  const res = await fetch(url, options);
  if (!res.ok) {
    throw new HttpError(res);
  }
  return await res.json();
}

export function randomString(length) {
  const possible =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz1234567890";
  let result = "";
  for (let i = 0; i < length; i++) {
    result += possible.charAt(Math.floor(Math.random() * possible.length));
  }
  return result;
}

export async function sha256(string) {
  const binaryHash = await crypto.subtle.digest(
    "SHA-256",
    new TextEncoder("utf-8").encode(string)
  );
  return btoa(String.fromCharCode.apply(null, new Uint8Array(binaryHash)))
    .split("=")[0]
    .replace(/\+/g, "-")
    .replace(/\//g, "_");
}

function LoginPage() {
  const {
    activeDirectory: { openIdConnectUrl, client_id, domain_hint },
  } = useContext(ApiContext);
  async function handleLogin() {
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
      code_challenge: await sha256(code_verifier),
      code_challenge_method: "S256",
      redirect_uri: window.location.origin + "/login/callback",
      domain_hint,
    };

    window.location.href =
      authorization_endpoint + "?" + new URLSearchParams(payload);
  }

  return (
    <div>
      <button onClick={handleLogin}>Login</button>
    </div>
  );
}

class TokenResponse {
  access_token: string;
}

function LoginCallbackPage({
  onComplete,
}: {
  onComplete(tokenResponse: TokenResponse): void;
}) {
  const {
    activeDirectory: { openIdConnectUrl, client_id },
  } = useContext(ApiContext);
  const hash = Object.fromEntries(
    new URLSearchParams(window.location.hash.substr(1))
  );
  const { error, error_description, state, code } = hash;
  const loginState = JSON.parse(sessionStorage.getItem("loginState"));

  useEffect(() => {
    (async () => {
      if (code && loginState) {
        const { token_endpoint } = await fetchJson(openIdConnectUrl);
        const { code_verifier } = loginState;
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
    })();
  }, [code]);

  if (state === null || state != loginState?.state) {
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

  console.log(hash);

  return <div>Please wait</div>;
}

function Application() {
  const [access_token, setAccessToken] = useState<string | undefined>();
  const history = useHistory();
  const basePath = "http://localhost:8080/petstore/api";
  const apis: ApplicationApis = {
    petApi: new PetApi(basePath),
    storeApi: new StoreApi(basePath),
    userApi: new UserApi(basePath),
  };
  const activeDirectory = {
    openIdConnectUrl:
      "https://login.microsoftonline.com/common/.well-known/openid-configuration",
    client_id: "55a62cf9-3f20-47e0-b61d-51f835fd5945",
    domain_hint: "soprasteria.com",
  };
  function handleLoginComplete(tokenResponse: TokenResponse) {
    setAccessToken(tokenResponse.access_token);
    history.push("/");
  }

  return (
    <ApiContext.Provider value={{ apis, activeDirectory }}>
      <header>
        <Link to={"/categories"}>Categories</Link>
        <Link to={"/pets"}>Pets</Link>
        <div className="divider" />
        <Link to={"/login"}>Login</Link>
      </header>
      <main>
        <Switch>
          <Route path={"/categories"}>
            <ListCategories />
          </Route>
          <Route path={"/login/callback"}>
            <LoginCallbackPage
              onComplete={(tokenResponse) => {
                setAccessToken(tokenResponse.access_token);
                history.push("/");
              }}
            />
          </Route>
          <Route path={"/login"}>
            <LoginPage />
          </Route>
          <Route>
            <h1>Not found</h1>
          </Route>
        </Switch>
      </main>
    </ApiContext.Provider>
  );
}

ReactDOM.render(
  <BrowserRouter>
    <Application />
  </BrowserRouter>,
  document.getElementById("app")
);
