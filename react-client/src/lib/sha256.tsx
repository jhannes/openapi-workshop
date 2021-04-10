export async function sha256(string: string) {
  const binaryHash = await crypto.subtle.digest(
    "SHA-256",
    new TextEncoder().encode(string)
  );
  // @ts-ignore
  return btoa(String.fromCharCode.apply(null, new Uint8Array(binaryHash)))
    .split("=")[0]
    .replace(/\+/g, "-")
    .replace(/\//g, "_");
}
