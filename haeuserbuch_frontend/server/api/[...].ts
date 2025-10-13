import { joinURL } from "ufo";

export default defineEventHandler((event) => {
    const proxyUrl = useRuntimeConfig().apiBaseUrl;
    const path = event.path.replace(/^\/api/, '');
    const target = joinURL(proxyUrl, path);

    return proxyRequest(event, target);
});
