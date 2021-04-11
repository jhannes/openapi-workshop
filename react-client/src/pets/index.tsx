import { Route, Switch } from "react-router";
import * as React from "react";
import { ListPets } from "./ListPets";
import { NewPet } from "./NewPet";

export function PetsPage() {
  return (
    <Switch>
      <Route path={"/pets/new"}>
        <NewPet />
      </Route>
      <Route path={"/pets"}>
        <ListPets />
      </Route>
    </Switch>
  );
}
