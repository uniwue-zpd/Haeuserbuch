/*
    Initializes all available stores
*/
export default defineNuxtPlugin(async (nuxtApp) => {
    const placeStore = usePlaceStore();
    const tileStore = useTileStore();

    try {
        await Promise.all([
            placeStore.fetchPlaces(),
            tileStore.fetchTiles()
        ]);
    } catch (error) {
        console.error("Error initializing stores:", error);
    }
});
