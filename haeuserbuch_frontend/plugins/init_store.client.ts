/*
    Initializes all available stores
*/
export default defineNuxtPlugin(async (nuxtApp) => {
    const buildingStore = useBuildingStore();
    const placeStore = usePlaceStore();
    const personStore = usePersonStore();
    const tileStore = useTileStore();

    await buildingStore.fetchBuildings();
    await placeStore.fetchPlaces();
    await personStore.fetchPersons();
    await tileStore.fetchTiles();
});
