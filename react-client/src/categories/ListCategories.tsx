import * as React from "react";
import { useContext } from "react";
import { ApiContext } from "../applicationContext";
import { useLoader } from "../lib/useLoader";

export function ListCategories() {
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
