/*
    Initializes all available stores
*/
export default defineNuxtPlugin(async (nuxtApp) => {
    const tileStore = useTileStore();

    try {
        await Promise.all([
            tileStore.fetchTiles()
        ]);
    } catch (error) {
        console.error("Error initializing stores:", error);
    }
});
