import { SyntheticEvent, useState } from "react";

export function useSubmit(
  submitFunction: () => Promise<void>,
  onComplete: () => void
) {
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<Error | undefined>();

  async function handleSubmit(e?: SyntheticEvent<unknown>) {
    if (e) {
      e.preventDefault();
    }
    setSubmitError(undefined);
    setSubmitting(true);
    try {
      await submitFunction();
      onComplete();
    } catch (e) {
      setSubmitError(e);
    } finally {
      setSubmitting(false);
    }
  }

  return { handleSubmit, submitting, submitError };
}
