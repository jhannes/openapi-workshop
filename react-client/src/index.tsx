import * as React from "react";
import { useContext } from "react";
import * as ReactDOM from "react-dom";
import {
  ad,
  ApplicationApis,
  petstore_auth,
  servers,
} from "@jhannes/openapi-workshop";
import { BrowserRouter, Link } from "react-router-dom";
import { Route, Switch, useHistory } from "react-router";
import { useLoader } from "./lib/useLoader";
import { activeDirectory, ApiContext } from "./applicationContext";
import { TokenResponse } from "./login/LoginCallbackPage";
import { ErrorView } from "./views/ErrorView";
import { useLocalStorage } from "./lib/useLocalStorage";
import { LoginPage } from "./login/LoginPage";
import { PetsPage } from "./pets";

function ListCategories() {
  const {
    apis: { petApi },
  } = useContext(ApiContext);

  const categories = useLoader(async () => await petApi.listCategories());

  if (categories.loading) {
    return <div>Loading...</div>;
  }
  if (categories.failed) {
    return <div>Error: {categories.error.toString()}</div>;
  }
  return (
    <ul>
      {categories.data.map(({ id, name }) => (
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
  const userInfo = useLoader(async () => {
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

  if (userInfo.loading) {
    return <ApplicationLoading />;
  }
  if (userInfo.failed) {
    return <ErrorView error={userInfo.error} />;
  }

  return (
    <ApiContext.Provider
      value={{ apis, activeDirectory, security: new petstore_auth("") }}
    >
      <header>
        <Link to={"/categories"}>Categories</Link>
        <Link to={"/pets"}>Pets</Link>
        <div className="divider" />
        {userInfo.data ? (
          <Link to={"/profile"}>{userInfo.data.firstName}</Link>
        ) : (
          <Link to={"/login/authorize"}>Login</Link>
        )}
      </header>
      <main>
        <Switch>
          <Route path={"/categories"}>
            <ListCategories />
          </Route>
          <Route path={"/pets"}>
            <PetsPage />
          </Route>
          <Route path={"/login"}>
            <LoginPage
              onComplete={handleLoginComplete}
              provider={activeDirectory}
            />
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
