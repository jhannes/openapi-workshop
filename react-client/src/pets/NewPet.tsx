import * as React from "react";
import { useContext, useState } from "react";
import { ApiContext } from "../applicationContext";
import { useHistory } from "react-router";
import {
  PetDtoStatusDtoEnum,
  PetDtoStatusDtoEnumValues,
} from "@jhannes/openapi-workshop";
import { useLoader } from "../lib/useLoader";
import { useSubmit } from "../lib/useSubmit";
import { LoadingView } from "../views/LoadingView";
import { ErrorView } from "../views/ErrorView";
import { FormInputField } from "../views/FormInputField";
import { FormField } from "../views/FormField";

export function NewPet() {
  const {
    apis: { petApi },
    security,
  } = useContext(ApiContext);
  const history = useHistory();

  const [status, setStatus] = useState<PetDtoStatusDtoEnum>("available");
  const [categoryId, setCategoryId] = useState("");
  const [name, setName] = useState("");
  const [tags, setTags] = useState<string[]>([]);

  const categories = useLoader(async () => {
    return await petApi.listCategories();
  });

  const { handleSubmit, submitting, submitError } = useSubmit(
    async () => {
      return await petApi.addPet({
        petDto: { status, category: { id: categoryId }, name, tags },
        security,
      });
    },
    () => {
      history.push("/pets");
    }
  );

  if (categories.loading) {
    return <LoadingView />;
  }
  if (categories.failed) {
    return <ErrorView error={categories.error} />;
  }

  return (
    <div>
      <h1>New pet</h1>
      <form onSubmit={handleSubmit}>
        <fieldset disabled={submitting}>
          <FormInputField label={"Name"} value={name} onChangeValue={setName} />
          <FormField label={"Status"}>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value as PetDtoStatusDtoEnum)}
            >
              {PetDtoStatusDtoEnumValues.map((value) => (
                <option key={value} value={value}>
                  {value}
                </option>
              ))}
            </select>
          </FormField>
          <FormField label={"Category"}>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)}
            >
              <option value={""} />
              {categories.data.map(({ id, name }) => (
                <option key={id} value={id}>
                  {name}
                </option>
              ))}
            </select>
          </FormField>
          {submitError && <div>Error: {submitError.toString()}</div>}
          <button>Submit</button>
        </fieldset>
      </form>
    </div>
  );
}
