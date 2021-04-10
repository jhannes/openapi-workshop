import { HttpError } from "@jhannes/openapi-workshop/dist/base";

export async function fetchJson(
  url: string,
  options?: RequestInit
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Promise<any> {
  const res = await fetch(url, options);
  if (!res.ok) {
    throw new HttpError(res);
  }
  return await res.json();
}
