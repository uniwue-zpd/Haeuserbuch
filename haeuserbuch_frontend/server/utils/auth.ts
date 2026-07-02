import type { H3Event } from 'h3';

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
