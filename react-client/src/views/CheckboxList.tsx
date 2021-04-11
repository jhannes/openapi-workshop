import * as React from "react";
import { useEffect, useState } from "react";

export function CheckboxList<T extends string>({
  values,
  setValues,
}: {
  values: T[];
  setValues(values: T[]): void;
}) {
  const [checked, setChecked] = useState<Partial<Record<string, boolean>>>({});
  useEffect(() => {
    setValues(values.filter((c: string) => checked[c]));
  }, [checked]);

  return (
    <div>
      {values.map((value) => (
        <label key={value}>
          <input
            type="checkbox"
            className="type"
            checked={checked[value] || false}
            onChange={(e) =>
              setChecked({ ...checked, [value]: e.target.checked })
            }
          />
          {value}
        </label>
      ))}
    </div>
  );
}
