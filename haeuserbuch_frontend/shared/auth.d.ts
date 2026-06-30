declare module '#auth-utils' {
    interface SecureSessionData {
        accessToken: string;
        refreshToken: string;
        accessTokenExpires: number;
    }
}

export {};
