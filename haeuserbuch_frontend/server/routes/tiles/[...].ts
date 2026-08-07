import {
    defineEventHandler,
    proxyRequest,
    removeResponseHeader,
    setResponseHeader,
    type H3Event,
} from 'h3';
import { joinURL } from 'ufo';

export default defineEventHandler((event: H3Event) => {
    const proxyUrl = useRuntimeConfig(event).tileserverApiUrl;
    const path = event.path.replace(/^\/tiles/, '');

    return proxyRequest(event, joinURL(proxyUrl, path), {
        onResponse(proxyEvent, response) {
            const isTileResource = path.startsWith('/data/') || path.startsWith('/styles/');
            if (!isTileResource || !response.ok) return;
            removeResponseHeader(proxyEvent, 'connection');
            setResponseHeader(
                proxyEvent,
                'cache-control',
                'public, max-age=86400, stale-while-revalidate=604800',
            );
        },
    });
});
