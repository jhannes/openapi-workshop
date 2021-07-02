import { act, Simulate } from "react-dom/test-utils";
import { ListPets } from "../ListPets";
import * as React from "react";
import { activeDirectory } from "@jhannes/openapi-workshop";
import { mockPetApi } from "@jhannes/openapi-workshop/dist/test/apiTest";
import { NewPet } from "../NewPet";
import {
  renderApplication,
  simulateCheck,
  simulateFormChange,
  testData,
} from "../../__mocks__/applicationTest";
import pretty = require("pretty");

describe("pets screens", () => {
  it("shows new pet screen", async () => {
    const sampleData = testData(100);
    const container = await renderApplication(<NewPet />, {
      petApi: mockPetApi({
        listCategories: async () => sampleData.sampleArrayCategoryDto(),
      }),
    });
    expect(pretty(container.innerHTML)).toMatchSnapshot();
  });

  it("submits new pet", async () => {
    const sampleData = testData(200);
    const categories = sampleData.sampleArrayCategoryDto();
    const petDto = {
      name: sampleData.samplePetDto().name,
      category: { id: categories[1].id },
      status: sampleData.samplePetDto().status,
    };
    const addPet = jest.fn();
    const security = new activeDirectory(sampleData.uuidv4());
    const container = await renderApplication(
      <NewPet />,
      {
        petApi: mockPetApi({
          addPet,
          listCategories: async () => categories,
        }),
      },
      security
    );
    simulateFormChange(container, "Name", petDto.name);
    simulateFormChange(container, "Category", petDto.category.id);
    simulateFormChange(container, "Status", petDto.status);
    await act(async () => {
      Simulate.submit(container.querySelector("form")!);
    });
    expect(addPet).toHaveBeenCalledWith({ petDto, security });
  });

  it("shows pet list", async () => {
    const sampleData = testData(200);
    const container = await renderApplication(<ListPets />, {
      petApi: mockPetApi({
        findPetsByStatus: async () => sampleData.sampleArrayPetDto(),
      }),
    });
    expect(pretty(container.innerHTML)).toMatchSnapshot();
  });

  it("filters pet list", async () => {
    const sampleData = testData(200);
    const findPetsByStatus = jest.fn(async () =>
      sampleData.sampleArrayPetDto()
    );
    const container = await renderApplication(<ListPets />, {
      petApi: mockPetApi({ findPetsByStatus }),
    });
    await act(async () => {
      simulateCheck(container, "available", true);
    });
    expect(findPetsByStatus).toHaveBeenCalledWith({
      queryParams: { status: ["available"] },
    });
  });

  it("shows errors on new pet screen", async () => {
    const container = await renderApplication(<NewPet />, {
      petApi: mockPetApi({
        listCategories: async () => {
          throw new Error("Something went wrong");
        },
      }),
    });
    expect(pretty(container.innerHTML)).toMatchSnapshot();
  });

  it("shows errors on pet list", async () => {
    const container = await renderApplication(<ListPets />, {
      petApi: mockPetApi({
        findPetsByStatus: async () => {
          throw new Error("Something went wrong");
        },
      }),
    });
    expect(pretty(container.innerHTML)).toMatchSnapshot();
  });
});
