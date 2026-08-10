import type { H3Event } from 'h3';

/**
 * Gets current access token. If the token is expired, it will try to refresh it using the refresh token.
 * @param event `H3Event`
 * @returns Access token
 */
export const getAccessToken = async (event: H3Event) => {
    const session = await getUserSession(event);
    if (!session.secure?.accessToken) {
        throw createError({
            statusCode: 401,
            message: 'Unauthorized',
        })
    }
    const expiresAt = session.secure.accessTokenExpires ?? 0;
    const now = Date.now();

    if (expiresAt > now + 60_000) {
        return session.secure.accessToken;
    }

    if (!session.secure.refreshToken) {
        throw createError({
            statusCode: 401,
            message: 'Session expired',
        })
    }

    const config = useRuntimeConfig(event);
    const kc = config.oauth.keycloak;

    const response = await $fetch<{
        access_token: string
        refresh_token?: string
        expires_in: number
    }>(
        `${kc.serverUrl}/realms/${kc.realm}/protocol/openid-connect/token`,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                    grant_type: 'refresh_token',
                    client_id: kc.clientId,
                    client_secret: kc.clientSecret,
                    refresh_token: session.secure.refreshToken
            })
        }
    );
    await replaceUserSession(event, {
        user: session.user,
        secure: {
            accessToken: response.access_token,
            refreshToken: response.refresh_token ?? session.secure.refreshToken,
            accessTokenExpires: Date.now() + response.expires_in * 1000
        },
    });
    return response.access_token;
}

/**
 * Clears current auth session and logs out the user from Keycloak.
 * @param event `H3Event`
 * @returns Success status
 */
export const logoutUser = async (event: H3Event) => {
    const session = await getUserSession(event);
    const config = useRuntimeConfig(event);
    try {
        const keycloakConfig = config.oauth.keycloak;
        if (session.secure?.refreshToken) {
            const logoutURL =
                `${keycloakConfig.serverUrl}/realms/${keycloakConfig.realm}/protocol/openid-connect/logout`;
            await $fetch(logoutURL, {
                method: 'POST',
                body: new URLSearchParams({
                    client_id: keycloakConfig.clientId,
                    client_secret: keycloakConfig.clientSecret,
                    refresh_token: session.secure.refreshToken
                })
            });
        }
    } catch (error) {
        console.error('Error during logout from Keycloak:', error);
    }
    await clearUserSession(event);
    return { success: true };
}
