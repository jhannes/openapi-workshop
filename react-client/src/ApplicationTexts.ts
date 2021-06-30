import { useContext } from "react";
import { AppLocale, LocaleContext } from "./applicationLocale";
import { PetDtoStatusDtoEnum } from "@jhannes/openapi-workshop";

interface ApplicationTexts {
  standardTexts: {
    errorHeader: string;
    loading: string;
  };
  petstoreTexts: {
    actionCategories: string;
    actionLogin: string;
    actionLogout: string;
    actionPets: string;
    changeLanguage: string;
    createPet: string;
    notFound: string;
    showPets: string;
    statuses: Record<PetDtoStatusDtoEnum, string>;
    loggedIn(args: { firstName: string; lastName: string }): string;
  };
  languages: Record<AppLocale, string>;
}

export function getLanguages(
  languages: Record<AppLocale, string>
): { locale: AppLocale; label: string }[] {
  const { sortBy } = useContext(LocaleContext);
  return Object.entries(languages)
    .sort(sortBy(([, b]) => b))
    .map(([locale, label]) => ({ locale: locale as AppLocale, label }));
}

const nb: ApplicationTexts = {
  standardTexts: {
    errorHeader: "Det har inntruffet en feil",
    loading: "Vennligst vent",
  },
  petstoreTexts: {
    actionCategories: "Kategorier",
    actionLogin: "Logg inn",
    actionLogout: "Logg ut",
    actionPets: "Kjæledyr",
    changeLanguage: "Bytt språk",
    createPet: "Opprett ny",
    notFound: "Siden finnes ikke",
    showPets: "Vis kjæledyr",
    statuses: {
      sold: "Solgt",
      pending: "Venter",
      available: "Tilgjengelig",
    },

    loggedIn: ({ firstName, lastName }) =>
      `Logget inn som ${firstName} ${lastName}`,
  },
  languages: {
    en: "Engelsk",
    nb: "Norsk",
  },
};

const en: ApplicationTexts = {
  standardTexts: {
    errorHeader: "An error has occurred",
    loading: "Please wait",
  },
  petstoreTexts: {
    actionCategories: "Categories",
    actionLogin: "Login",
    actionLogout: "Logout",
    actionPets: "Pets",
    changeLanguage: "Change language",
    createPet: "Create pet",
    notFound: "Page not found",
    showPets: "Show pets",

    statuses: {
      sold: "Sold",
      pending: "Pending",
      available: "Available",
    },

    loggedIn: ({ firstName, lastName }) =>
      `Logged in as ${firstName} ${lastName}`,
  },
  languages: {
    en: "English",
    nb: "Norwegian",
  },
};

const applicationTexts = { nb, en };

export function useApplicationTexts(): ApplicationTexts {
  const { locale } = useContext(LocaleContext);
  return applicationTexts[locale];
}
