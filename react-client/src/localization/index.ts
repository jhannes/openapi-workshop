import { useContext } from "react";
import { AppLocale, LocaleContext } from "./applicationLocale";
import { ApplicationTexts } from "./applicationTexts";

import nb from "./applicationTexts.nb";
import en from "./applicationTexts.en";

const applicationTexts: Record<AppLocale, ApplicationTexts> = { nb, en };

export function useApplicationTexts(): ApplicationTexts {
  const { locale } = useContext(LocaleContext);
  return applicationTexts[locale];
}

export function getLanguages(
  languages: Record<AppLocale, string>
): { locale: AppLocale; label: string }[] {
  const { sortBy } = useContext(LocaleContext);
  return Object.entries(languages)
    .sort(sortBy(([, b]) => b))
    .map(([locale, label]) => ({ locale: locale as AppLocale, label }));
}
