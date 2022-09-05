import * as React from "react";
import { useContext, useState } from "react";
import { PetDtoStatusEnum } from "../generated";
import { ApiContext } from "../applicationContext";
import { useLoader } from "../lib/useLoader";
import { Link } from "react-router-dom";
import { CheckboxList } from "../views/CheckboxList";
import { LoadingView } from "../views/LoadingView";
import { ErrorView } from "../views/ErrorView";
import { useApplicationTexts } from "../localization";

function ShowPetList({ statuses }: { statuses: PetDtoStatusEnum[] }) {
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
  const [statuses, setStatuses] = useState<PetDtoStatusEnum[]>([]);

  const { petstoreTexts: texts } = useApplicationTexts();

  return (
    <div>
      <h1>{texts.showPets}</h1>
      <Link to={"/pets/new"}>
        <button>{texts.createPet}</button>
      </Link>
      <CheckboxList values={texts.statuses} setValues={setStatuses} />
      <ShowPetList statuses={statuses} />
    </div>
  );
}
