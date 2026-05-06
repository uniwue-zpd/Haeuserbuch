/*
    Initializes all available stores
*/
export default defineNuxtPlugin(async (nuxtApp) => {
    const buildingStore = useBuildingStore();
    const placeStore = usePlaceStore();
    const sourceStore = useSourceStore();
    const tileStore = useTileStore();
    const streetStore = useStreetStore();
    const religionStore = useReligionStore();
    const weaponStore = useWeaponStore();

    try {
        await Promise.all([
            buildingStore.fetchBuildings(),
            placeStore.fetchPlaces(),
            sourceStore.fetchSources(),
            tileStore.fetchTiles(),
            streetStore.fetchStreets(),
            religionStore.fetchReligions(),
            weaponStore.fetchWeapons()
        ]);
    } catch (error) {
        console.error("Error initializing stores:", error);
    }
});
