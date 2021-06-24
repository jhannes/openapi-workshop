import * as React from "react";
import { ReactElement, ReactNode } from "react";
import { act, Simulate } from "react-dom/test-utils";
import ReactDOM from "react-dom";
import { ApplicationApis } from "@jhannes/openapi-workshop/dist/api";
import { activeDirectory } from "@jhannes/openapi-workshop";
import { mockApplicationApis } from "@jhannes/openapi-workshop/dist/test/apiTest";
import { ApiContext, identityProvider } from "../applicationContext";
import { MemoryRouter } from "react-router";
import {
  SampleModelFactories,
  TestSampleData,
} from "@jhannes/openapi-workshop/dist/test/modelTest";

export async function render(component: ReactElement) {
  const container = document.createElement("div");
  await act(async () => {
    ReactDOM.render(component, container);
  });
  return container;
}

export async function renderApplication(
  component: ReactNode,
  apis: Partial<ApplicationApis> = {},
  security: activeDirectory = new activeDirectory("")
) {
  const context = {
    apis: mockApplicationApis(apis),
    identityProvider,
    security,
  };
  return await render(
    <ApiContext.Provider value={context}>
      <MemoryRouter>{component}</MemoryRouter>
    </ApiContext.Provider>
  );
}

export function simulateFormChange(
  container: Element,
  label: string,
  value?: string
) {
  const element = container.querySelector(
    `[data-label=${label}] input, [data-label=${label}] select`
  );
  if (!element) {
    expect(`Could not find label[data-label=${label}]`).toBeUndefined();
    return;
  }
  const target: any = { value };
  Simulate.change(element, { target });
}

export function simulateCheck(
  container: Element,
  label: string,
  checked: boolean
) {
  const element = container.querySelector(
    `[data-label=${label}] input[type=checkbox]`
  );
  if (!element) {
    expect(`Could not find label[data-label=${label}]`).toBeUndefined();
    return;
  }
  const target: any = { checked };
  Simulate.change(element, { target });
}

export function testData(seed: number) {
  const sampleModelProperties: SampleModelFactories = {
    CategoryDto: {
      name: (sample) => sample.pickOne(["Cat", "Dog", "Bird", "Fish"]),
    },
    PetDto: {
      name: (sample) =>
        sample.pickOne(["Buddy", "Rocky", "Chase", "Daisy", "Tiger"]),
    },
  };
  return new TestSampleData({ seed, sampleModelProperties });
}
