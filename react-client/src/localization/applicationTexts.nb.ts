import { ApplicationTexts } from "./applicationTexts";

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

export default nb;
