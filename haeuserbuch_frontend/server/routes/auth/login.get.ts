export default defineOAuthKeycloakEventHandler({
    async onSuccess(event, { user, tokens }) {
        await setUserSession(event, {
            maxAge: 60 * 60 * 24, // 1 day
            user: {
                id: user.id,
                name: user.name,
                email: user.email
            },
            secure: {
                accessToken: tokens.access_token,
                refreshToken: tokens.refresh_token,
                accessTokenExpires: Date.now() + tokens.expires * 1000
            }
        });
        return sendRedirect(event, '/');
    },
    onError(event, error) {
        console.error('An error occurred: ', error);
        return sendRedirect(event, '/');
    }
});
