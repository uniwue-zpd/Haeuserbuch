import { defineEventHandler, proxyRequest, type H3Event } from 'h3';
import { joinURL } from 'ufo';

export default defineEventHandler((event: H3Event) => {
    const proxyUrl = useRuntimeConfig(event).tileserverApiUrl;
    const path = event.path.replace(/^\/tiles/, '');

    return proxyRequest(event, joinURL(proxyUrl, path));
});
