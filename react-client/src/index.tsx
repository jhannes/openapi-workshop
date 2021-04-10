import * as React from "react";
import * as ReactDOM from "react-dom";
import {
  ApplicationApis,
  PetApi,
  petstore_auth,
  servers,
  StoreApi,
  UserApi,
} from "@jhannes/openapi-workshop";
import { useContext, useEffect, useState } from "react";
import { BrowserRouter, Link } from "react-router-dom";
import { Route, Switch } from "react-router";

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

const ApiContext = React.createContext<{ apis: ApplicationApis }>({
  apis: servers.current,
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

function Application() {
  const basePath = "http://localhost:8080/petstore/api";
  const apis: ApplicationApis = {
    petApi: new PetApi(basePath),
    storeApi: new StoreApi(basePath),
    userApi: new UserApi(basePath),
  };
  return (
    <ApiContext.Provider value={{ apis }}>
      <BrowserRouter>
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
            <Route>
              <h1>Not found</h1>
            </Route>
          </Switch>
        </main>
      </BrowserRouter>
    </ApiContext.Provider>
  );
}

ReactDOM.render(<Application />, document.getElementById("app"));
