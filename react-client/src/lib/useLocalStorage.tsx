import { useEffect, useState } from "react";

export function useLocalStorage<T extends string | (string | undefined)>(
  key: string,
  initialValue: T
): [T, (value: T) => void] {
  const [value, setValue] = useState<T>(
    () => (localStorage.getItem(key) as T) || initialValue
  );
  useEffect(() => {
    if (value) {
      localStorage.setItem(key, value as string);
    } else {
      localStorage.removeItem(key);
    }
  }, [key, value]);
  useEffect(() => {
    window.addEventListener("storage", (event) => {
      if (event.key === key && event.newValue !== event.oldValue) {
        setValue((event.newValue as T) || initialValue);
      } else if (event.key === null) {
        // local storage was cleared
        setValue(initialValue);
      }
    });
  }, [key]);

  return [value, setValue];
}
