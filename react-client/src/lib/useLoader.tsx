import { DependencyList, useEffect, useState } from "react";

type LoadingState<T> =
  | { failed: false; loading: true }
  | { failed: false; loading: false; data: T }
  | { failed: true; loading: false; error: Error };

export function useLoader<T>(
  loadingFunction: () => Promise<T>,
  deps: DependencyList = []
): LoadingState<T> {
  const [state, setState] = useState<LoadingState<T>>({
    loading: true,
    failed: false,
  });

  useEffect(() => {
    (async () => {
      setState({ loading: true, failed: false });
      try {
        setState({
          loading: false,
          failed: false,
          data: await loadingFunction(),
        });
      } catch (error) {
        console.warn(error);
        setState({ failed: true, loading: false, error: error as Error });
      }
    })();
  }, deps);

  return state;
}
