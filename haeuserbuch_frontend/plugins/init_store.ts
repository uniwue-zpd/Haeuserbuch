/*
    Initializes all available stores
*/
export default defineNuxtPlugin(async (nuxtApp) => {
    const buildingStore = useBuildingStore();
    const placeStore = usePlaceStore();
    const tileStore = useTileStore();
    const streetStore = useStreetStore();

    try {
        await Promise.all([
            buildingStore.fetchBuildings(),
            placeStore.fetchPlaces(),
            tileStore.fetchTiles(),
            streetStore.fetchStreets()
        ]);
    } catch (error) {
        console.error("Error initializing stores:", error);
    }
});
