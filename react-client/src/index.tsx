import * as React from "react";
import * as ReactDOM from "react-dom";
import {
  ApplicationApis,
  PetApi,
  petstore_auth,
  servers,
} from "@jhannes/openapi-workshop";
import { useEffect, useState } from "react";

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
  const petApi = new PetApi("http://localhost:8080/petstore/api");

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
  return <ListCategories />;
}

ReactDOM.render(<Application />, document.getElementById("app"));
