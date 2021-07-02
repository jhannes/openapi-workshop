import * as React from "react";
import { useApplicationTexts } from "../localization";

export function LoadingView() {
  const { standardTexts: texts } = useApplicationTexts();
  return <div>{texts.loading}</div>;
}
