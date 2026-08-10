import { defineEventHandler, proxyRequest, type H3Event } from 'h3';
import { joinURL } from 'ufo';

export default defineEventHandler((event: H3Event) => {
    const proxyUrl = useRuntimeConfig(event).apiBaseUrl;

    return proxyRequest(event, joinURL(proxyUrl, event.path));
});
