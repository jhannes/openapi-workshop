import * as React from "react";
import { ReactNode } from "react";

export function FormField({
  label,
  children,
}: {
  label: string;
  children: ReactNode;
}) {
  return (
    <div>
      <label>
        {label}: {children}
      </label>
    </div>
  );
}
