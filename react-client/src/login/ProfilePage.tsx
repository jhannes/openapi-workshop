import * as React from "react";
import { UserDto } from "@jhannes/openapi-workshop";

export function ProfilePage({
  userInfo,
  onLogOut,
}: {
  userInfo: UserDto | null;
  onLogOut(): void;
}) {
  if (!userInfo) {
    return null;
  }
  return (
    <>
      <h1>
        Logged in as {userInfo.firstName} {userInfo.lastName}
      </h1>
      <div>
        <button onClick={onLogOut}>Log out</button>
      </div>
      <div>
        <h2>Change language</h2>
        <div>
          <button>English</button>
        </div>
        <div>
          <button>Norwegian</button>
        </div>
      </div>
    </>
  );
}
