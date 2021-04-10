import * as React from "react";
import { useContext, useState } from "react";
import * as ReactDOM from "react-dom";
import {
  ApplicationApis,
  PetApi,
  PetDtoStatusDtoEnum,
  petstore_auth,
  StoreApi,
  UserApi,
} from "@jhannes/openapi-workshop";
import { BrowserRouter, Link } from "react-router-dom";
import { Route, Switch, useHistory } from "react-router";
import { useLoader } from "./lib/useLoader";
import { activeDirectory, ApiContext } from "./applicationContext";
import { LoginPage } from "./login/LoginPage";
import { LoginCallbackPage, TokenResponse } from "./login/LoginCallbackPage";

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

function Application() {
  const [access_token, setAccessToken] = useState<string | undefined>();
  const history = useHistory();
  const basePath = "http://localhost:8080/petstore/api";
  const apis: ApplicationApis = {
    petApi: new PetApi(basePath),
    storeApi: new StoreApi(basePath),
    userApi: new UserApi(basePath),
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
