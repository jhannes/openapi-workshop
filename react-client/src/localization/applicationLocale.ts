/* eslint-disable @typescript-eslint/no-explicit-any */
import * as React from "react";

export type AppLocale = "en" | "nb";

type SortByFunction = (
  fn: (value: any) => string
) => (a: any, b: any) => number;

export const LocaleContext = React.createContext<{
  locale: AppLocale;
  sortBy: SortByFunction;
}>({
  locale: "en",
  sortBy: (fn) => (a, b) => fn(a).localeCompare(fn(b)),
});
