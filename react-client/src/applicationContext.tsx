import * as React from "react";
import {
  ApplicationApis,
  activeDirectory,
  servers,
} from "@jhannes/openapi-workshop";

export const identityProvider = {
  openIdConnectUrl:
    "https://login.microsoftonline.com/common/.well-known/openid-configuration",
  client_id: "55a62cf9-3f20-47e0-b61d-51f835fd5945",
  domain_hint: "soprasteria.com",
};

export interface OpenIdConnectProvider {
  openIdConnectUrl: string;
  client_id: string;
  domain_hint?: string;
}

export const ApiContext = React.createContext<{
  apis: ApplicationApis;
  identityProvider: OpenIdConnectProvider;
  security: activeDirectory;
}>({
  apis: servers.production,
  identityProvider,
  security: new activeDirectory(""),
});
