import { createError, defineEventHandler, getHeader, proxyRequest } from 'h3'
import { joinURL } from 'ufo'

export default defineEventHandler(async (event) => {
    const authorization = getHeader(event, 'authorization');

    if (!authorization?.startsWith('Bearer ')) {
        throw createError({
            statusCode: 401,
            statusMessage: 'Unauthorized'
        });
    }

    const path = event.path.replace(/^\/api-service/, '')
    const target = joinURL(useRuntimeConfig(event).apiBaseUrl, path)

    return proxyRequest(event, target, {
        headers: {
            authorization
        }
    });
});
