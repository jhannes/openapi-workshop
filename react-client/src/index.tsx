import * as React from "react";
import { useEffect } from "react";
import * as ReactDOM from "react-dom";
import {
  activeDirectory,
  ApplicationApis,
  PetApi,
  StoreApi,
  UserApi,
} from "@jhannes/openapi-workshop";
import { BrowserRouter, Link } from "react-router-dom";
import { Route, Switch, useHistory } from "react-router";
import { useLoader } from "./lib/useLoader";
import { ApiContext, identityProvider } from "./applicationContext";
import { TokenResponse } from "./login/LoginCallbackPage";
import { ErrorView } from "./views/ErrorView";
import { useLocalStorage } from "./lib/useLocalStorage";
import { LoginPage } from "./login/LoginPage";
import { PetsPage } from "./pets";
import { LoggedOutError } from "@jhannes/openapi-workshop/dist/base";
import { ListCategories } from "./categories/ListCategories";
import { ProfilePage } from "./login/ProfilePage";

function ApplicationLoading() {
  return <div>Please wait</div>;
}

function Application() {
  const [access_token, setAccessToken] = useLocalStorage("access_token");
  const history = useHistory();
  const basePath = "http://localhost:8080/petstore/api";
  //const basePath = "https://openapi-workshop.azurewebsites.net/petstore/api";
  const options = access_token
    ? {
        headers: {
          Authorization: `Bearer ${access_token}`,
        },
      }
    : undefined;
  const apis: ApplicationApis = {
    petApi: new PetApi(basePath, options),
    storeApi: new StoreApi(basePath, options),
    userApi: new UserApi(basePath, options),
  };
  const userInfo = useLoader(async () => {
    return access_token
      ? await apis.userApi.getCurrentUser({
          security: new activeDirectory(access_token),
        })
      : null;
  }, [access_token]);
  useEffect(() => {
    if (userInfo.failed && userInfo.error instanceof LoggedOutError) {
      console.log(userInfo.error.response.status);
      setAccessToken(undefined);
    }
  }, [userInfo.failed]);

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
      value={{
        apis,
        identityProvider,
        security: new activeDirectory(access_token || ""),
      }}
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
              provider={identityProvider}
            />
          </Route>
          <Route path={"/profile"}>
            <ProfilePage
              userInfo={userInfo.data}
              onLogOut={() => setAccessToken(undefined)}
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
