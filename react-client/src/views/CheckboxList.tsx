import * as React from "react";
import { useContext, useEffect, useState } from "react";
import { LocaleContext } from "../applicationLocale";

function Checkbox({
  checked,
  label,
  onChangeChecked,
  value,
}: {
  value: string;
  checked: Partial<Record<string, boolean>>;
  onChangeChecked: (value: boolean) => void;
  label: string | undefined;
}) {
  return (
    <label data-label={value}>
      <input
        type="checkbox"
        className="type"
        checked={checked[value] || false}
        onChange={(e) => onChangeChecked(e.target.checked)}
      />
      {label || value}
    </label>
  );
}

function recordToArray<T extends string>(
  record: Record<T, string>
): { key: T; value: string }[] {
  const { sortBy } = useContext(LocaleContext);
  return Object.entries(record)
    .sort(sortBy(([, b]) => b))
    .map(([key, value]) => ({ key: key as T, value: value as string }));
}

export function CheckboxList<T extends string>({
  values,
  setValues,
}: {
  values: Record<T, string>;
  setValues(values: T[]): void;
}) {
  const [checked, setChecked] = useState<Partial<Record<string, boolean>>>({});
  useEffect(() => {
    setValues(Object.keys(values).filter((c) => checked[c]) as T[]);
  }, [checked]);

  return (
    <div>
      {recordToArray(values).map(({ key, value }) => (
        <Checkbox
          key={key}
          value={key}
          checked={checked}
          onChangeChecked={(b) => setChecked({ ...checked, [key]: b })}
          label={value}
        />
      ))}
    </div>
  );
}
