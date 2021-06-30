import * as React from "react";
import { useApplicationTexts } from "../ApplicationTexts";

export function LoadingView() {
  const { standardTexts: texts } = useApplicationTexts();
  return <div>{texts.loading}</div>;
}
