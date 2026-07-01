declare module '#auth-utils' {
    interface SecureSessionData {
        accessToken: string;
        refreshToken: string;
        accessTokenExpires: number;
    }

    interface User {
        id: string;
        name: string;
        email: string;
    }
}

export {};
