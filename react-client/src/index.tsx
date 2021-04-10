import * as React from "react";
import { useContext, useState } from "react";
import * as ReactDOM from "react-dom";
import {
  ad,
  ApplicationApis,
  PetApi,
  PetDtoStatusDtoEnum,
  petstore_auth,
  servers,
} from "@jhannes/openapi-workshop";
import { BrowserRouter, Link } from "react-router-dom";
import { Route, Switch, useHistory } from "react-router";
import { useLoader } from "./lib/useLoader";
import { activeDirectory, ApiContext } from "./applicationContext";
import { LoginPage } from "./login/LoginPage";
import { LoginCallbackPage, TokenResponse } from "./login/LoginCallbackPage";
import { ErrorView } from "./views/ErrorView";
import { useLocalStorage } from "./lib/useLocalStorage";

function AddPet() {
  const petApi = new PetApi();
  const [category, setCategory] = useState("");
  const [name, setName] = useState("");
  const [status, setStatus] = useState<PetDtoStatusDtoEnum>("available");

  const security = new petstore_auth("test");

  function handleSubmit() {
    petApi.addPet({
      petDto: { category: { id: category }, name, status },
      security,
    });
  }
}

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

function ApplicationLoading() {
  return <div>Please wait</div>;
}

function Application() {
  const [access_token, setAccessToken] = useLocalStorage("access_token");
  const history = useHistory();
  const apis: ApplicationApis = servers.localhost;
  const userInfoState = useLoader(async () => {
    return access_token
      ? await apis.userApi.getCurrentUser({
          security: new ad(access_token),
        })
      : null;
  }, [access_token]);

  function handleLoginComplete(tokenResponse: TokenResponse) {
    localStorage.setItem("tokenResponse", JSON.stringify(tokenResponse));
    setAccessToken(tokenResponse.access_token);
    history.push("/");
  }

  if (userInfoState.state === "loading") {
    return <ApplicationLoading />;
  }
  if (userInfoState.state === "error") {
    return <ErrorView error={userInfoState.error} />;
  }

  return (
    <ApiContext.Provider value={{ apis, activeDirectory }}>
      <header>
        <Link to={"/categories"}>Categories</Link>
        <Link to={"/pets"}>Pets</Link>
        <div className="divider" />
        {userInfoState.data ? (
          <Link to={"/profile"}>{userInfoState.data.firstName}</Link>
        ) : (
          <Link to={"/login"}>Login</Link>
        )}
      </header>
      <main>
        <Switch>
          <Route path={"/categories"}>
            <ListCategories />
          </Route>
          <Route path={"/login/callback"}>
            <LoginCallbackPage
              onComplete={handleLoginComplete}
              provider={activeDirectory}
            />
          </Route>
          <Route path={"/login"}>
            <LoginPage provider={activeDirectory} />
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
