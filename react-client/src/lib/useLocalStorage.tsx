import { useEffect, useState } from "react";

export function useLocalStorage(
  key: string,
  initialValue?: string
): [string | undefined, (value: string) => void] {
  const [value, setValue] = useState<string | undefined>(
    () => localStorage.getItem(key) || initialValue
  );
  useEffect(() => {
    if (!value) {
      localStorage.removeItem(key);
    } else {
      localStorage.setItem(key, value);
    }
  }, [key, value]);
  useEffect(() => {
    window.addEventListener("storage", (event) => {
      if (event.key === key && event.newValue !== value) {
        setValue(event.newValue || undefined);
      } else if (event.key === null) {
        // local storage was cleared
        setValue(undefined);
      }
    });
  }, [key]);

  return [value, setValue];
}
