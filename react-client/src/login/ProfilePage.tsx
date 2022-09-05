import * as React from "react";
import { UserDto } from "../generated";
import { AppLocale } from "../localization/applicationLocale";
import { getLanguages, useApplicationTexts } from "../localization/";

export function ProfilePage({
  userInfo,
  setLocale,
  onLogOut,
}: {
  userInfo: UserDto | null;
  setLocale(value: AppLocale): void;
  onLogOut(): void;
}) {
  const { petstoreTexts: texts, languages } = useApplicationTexts();
  if (!userInfo) {
    return null;
  }
  return (
    <>
      <h1>{texts.loggedIn(userInfo)}</h1>
      <div>
        <button onClick={onLogOut}>{texts.actionLogout}</button>
      </div>
      <div>
        <h2>{texts.changeLanguage}</h2>
        {getLanguages(languages).map(({ locale, label }) => (
          <button key={locale} onClick={() => setLocale(locale as AppLocale)}>
            {label}
          </button>
        ))}
      </div>
    </>
  );
}
