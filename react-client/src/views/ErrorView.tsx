import * as React from "react";

export function ErrorView({ error }: { error: Error }) {
  return (
    <div>
      <h1>An error occurred</h1>
      <div>{error.toString()}</div>
    </div>
  );
}