import { PetDtoStatusEnum } from "@jhannes/openapi-workshop";
import { AppLocale } from "./applicationLocale";

export interface ApplicationTexts {
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
    statuses: Record<PetDtoStatusEnum, string>;
    loggedIn(args: { firstName: string; lastName: string }): string;
  };
  languages: Record<AppLocale, string>;
}
