import * as React from "react";
import { useContext } from "react";
import { ApiContext } from "../applicationContext";
import { useLoader } from "../lib/useLoader";
import { LoadingView } from "../views/LoadingView";
import { ErrorView } from "../views/ErrorView";

export function ListCategories() {
  const {
    apis: { petApi },
  } = useContext(ApiContext);

  const categories = useLoader(async () => await petApi.listCategories());

  if (categories.loading) {
    return <LoadingView />;
  }
  if (categories.failed) {
    return <ErrorView error={categories.error} />;
  }
  return (
    <ul>
      {categories.data.map(({ id, name }) => (
        <li key={id}>{name}</li>
      ))}
    </ul>
  );
}
