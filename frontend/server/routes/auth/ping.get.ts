export default defineEventHandler(async (event) => {
    await getAccessToken(event);
    return { isValidSession: true }
})
