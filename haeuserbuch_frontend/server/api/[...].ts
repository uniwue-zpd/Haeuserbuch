import { joinURL } from "ufo";
import { getAccessToken } from '#server/utils/auth'

export default defineEventHandler(async (event) => {
    const proxyUrl = useRuntimeConfig().apiBaseUrl;
    const path = event.path.replace(/^\/api/, '');
    const target = joinURL(proxyUrl, path);

    const method = event.method;
    if (method === "GET") return proxyRequest(event, target);

    // Protected routes require an access token
    const accessToken = await getAccessToken(event);

    return proxyRequest(event, target, {
        headers: {
            Authorization: `Bearer ${ accessToken }`,
        },
    });
});
