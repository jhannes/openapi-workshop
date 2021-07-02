import * as React from "react";
import { useContext, useEffect } from "react";
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
import { AppLocale, LocaleContext } from "./localization/applicationLocale";
import { useApplicationTexts } from "./localization";
import { LoadingView } from "./views/LoadingView";

function ApplicationFrame({
  onLogOut,
  onLoginComplete,
  setLocale,
}: {
  onLoginComplete: (tokenResponse: TokenResponse) => void;
  setLocale: (value: AppLocale) => void;
  onLogOut: () => void;
}) {
  const { apis, security } = useContext(ApiContext);

  const userInfo = useLoader(
    async () =>
      security ? await apis.userApi.getCurrentUser({ security }) : null,
    [security, apis]
  );
  useEffect(() => {
    if (userInfo.failed && userInfo.error instanceof LoggedOutError) {
      console.log(userInfo.error.response.status);
      onLogOut();
    }
  }, [userInfo.failed]);

  const { petstoreTexts: texts } = useApplicationTexts();

  if (userInfo.loading) {
    return <LoadingView />;
  }
  if (userInfo.failed) {
    return <ErrorView error={userInfo.error} />;
  }
  return (
    <>
      <header>
        <Link to={"/categories"}>{texts.actionCategories}</Link>
        <Link to={"/pets"}>{texts.actionPets}</Link>
        <div className="divider" />
        {userInfo.data ? (
          <Link to={"/profile"}>{userInfo.data.firstName}</Link>
        ) : (
          <Link to={"/login/authorize"}>{texts.actionLogin}</Link>
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
              onComplete={onLoginComplete}
              provider={identityProvider}
            />
          </Route>
          <Route path={"/profile"}>
            <ProfilePage
              userInfo={userInfo.data}
              setLocale={setLocale}
              onLogOut={onLogOut}
            />
          </Route>
          <Route>
            <h1>{texts.notFound}</h1>
          </Route>
        </Switch>
      </main>
    </>
  );
}

function Application() {
  const [access_token, setAccessToken] = useLocalStorage<string | undefined>(
    "access_token",
    undefined
  );
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

  function handleLoginComplete(tokenResponse: TokenResponse) {
    localStorage.setItem("tokenResponse", JSON.stringify(tokenResponse));
    setAccessToken(tokenResponse.access_token);
    history.push("/");
  }

  const [locale, setLocale] = useLocalStorage<AppLocale>("locale", "en");

  return (
    <ApiContext.Provider
      value={{
        apis,
        identityProvider,
        security: access_token
          ? new activeDirectory(access_token || "")
          : undefined,
      }}
    >
      <LocaleContext.Provider
        value={{
          locale,
          sortBy: (fn) => (a, b) => fn(a).localeCompare(fn(b), locale),
        }}
      >
        <ApplicationFrame
          onLoginComplete={handleLoginComplete}
          setLocale={setLocale}
          onLogOut={() => setAccessToken(undefined)}
        />
      </LocaleContext.Provider>
    </ApiContext.Provider>
  );
}

ReactDOM.render(
  <BrowserRouter>
    <Application />
  </BrowserRouter>,
  document.getElementById("app")
);
