import * as React from "react";
import { useApplicationTexts } from "../localization";

export function ErrorView({ error }: { error: Error }) {
  const { standardTexts: texts } = useApplicationTexts();
  return (
    <div>
      <h1>{texts.errorHeader}</h1>
      <div>{error.toString()}</div>
    </div>
  );
}
