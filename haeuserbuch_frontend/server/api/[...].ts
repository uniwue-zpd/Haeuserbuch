import { joinURL } from "ufo";

export default defineEventHandler(async (event) => {
    const proxyUrl = useRuntimeConfig().apiBaseUrl;
    const path = event.path.replace(/^\/api/, '');
    const target = joinURL(proxyUrl, path);

    const method = event.method;
    if (method === "GET") return proxyRequest(event, target);

    // Protected routes require an access token
    const { secure } = await getUserSession(event);
    const accessToken = secure?.accessToken;
    if (!accessToken) throw createError({ statusCode: 401, message: 'Missing access token' });

    return proxyRequest(event, target, {
        headers: {
            Authorization: `Bearer ${ accessToken }`,
        },
    });
});
