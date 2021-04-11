import { FormField } from "./FormField";
import * as React from "react";

export function FormInputField({
  label,
  onChangeValue,
  value,
}: {
  onChangeValue: (value: ((prevState: string) => string) | string) => void;
  label: string;
  value: string;
}) {
  return (
    <FormField label={label}>
      <input
        type="text"
        value={value}
        onChange={(e) => onChangeValue(e.target.value)}
      />
    </FormField>
  );
}
