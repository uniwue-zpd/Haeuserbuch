export default defineNuxtPlugin(async (nuxtApp) => {
    const session = useUserSession();
    if (!session.loggedIn.value) return;
    try {
        await $fetch('/auth/ping');
    } catch {
        await session.clear();
    }
})
