import { ApplicationTexts } from "./applicationTexts";

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

export default en;
