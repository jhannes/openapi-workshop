import { DependencyList, useEffect, useState } from "react";

type LoadingState<T> =
  | { state: "loading" }
  | { state: "complete"; data: T }
  | { state: "error"; error: Error };

export function useLoader<T>(
  loadingFunction: () => Promise<T>,
  deps: DependencyList = []
): LoadingState<T> {
  const [state, setState] = useState<LoadingState<T>>({ state: "loading" });

  useEffect(() => {
    (async () => {
      setState({ state: "loading" });
      try {
        setState({ state: "complete", data: await loadingFunction() });
      } catch (error) {
        console.warn(error);
        setState({ state: "error", error });
      }
    })();
  }, deps);

  return state;
}
