import { renderApplication, testData } from "../../__mocks__/applicationTest";
import { ListCategories } from "../ListCategories";
import pretty from "pretty";
import React from "react";
import { mockPetApi } from "../../generated/test/apiTest";

describe("categories views", () => {
  it("shows categories", async () => {
    const sampleData = testData(300);
    const container = await renderApplication(<ListCategories />, {
      petApi: mockPetApi({
        listCategories: async () => sampleData.sampleArrayCategoryDto(),
      }),
    });
    expect(pretty(container.innerHTML)).toMatchSnapshot();
  });

  it("shows errors", async () => {
    const container = await renderApplication(<ListCategories />, {
      petApi: mockPetApi({
        listCategories: async () => {
          throw new Error("Something went wrong");
        },
      }),
    });
    expect(pretty(container.innerHTML)).toMatchSnapshot();
  });
});
