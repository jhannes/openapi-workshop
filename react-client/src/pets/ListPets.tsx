import * as React from "react";
import { useContext, useState } from "react";
import {
  activeDirectory,
  PetDtoStatusDtoEnum,
  PetDtoStatusDtoEnumValues,
} from "@jhannes/openapi-workshop";
import { ApiContext } from "../applicationContext";
import { useLoader } from "../lib/useLoader";
import { Link } from "react-router-dom";
import { CheckboxList } from "../views/CheckboxList";
import { LoadingView } from "../views/LoadingView";
import { ErrorView } from "../views/ErrorView";

function ShowPetList({ statuses }: { statuses: PetDtoStatusDtoEnum[] }) {
  const {
    apis: { petApi },
  } = useContext(ApiContext);

  const pets = useLoader(
    () => petApi.findPetsByStatus({ queryParams: { status: statuses } }),
    [statuses]
  );

  if (pets.loading) {
    return <LoadingView />;
  }
  if (pets.failed) {
    return <ErrorView error={pets.error} />;
  }

  return (
    <div>
      {pets.data.map(({ id, name }) => (
        <div key={id}>{name}</div>
      ))}
    </div>
  );
}

export function ListPets() {
  const [statuses, setStatuses] = useState<PetDtoStatusDtoEnum[]>([]);

  return (
    <div>
      <h1>Show pets</h1>
      <Link to={"/pets/new"}>
        <button>Create new</button>
      </Link>
      <CheckboxList
        values={PetDtoStatusDtoEnumValues}
        setValues={setStatuses}
      />
      <ShowPetList statuses={statuses} />
    </div>
  );
}
