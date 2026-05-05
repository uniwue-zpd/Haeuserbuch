/*
    Initializes all available stores
*/
export default defineNuxtPlugin(async (nuxtApp) => {
    const buildingStore = useBuildingStore();
    const placeStore = usePlaceStore();
    const sourceStore = useSourceStore();
    const tileStore = useTileStore();
    const districtStore = useDistrictStore();
    const streetStore = useStreetStore();
    const quarterStore = useQuarterStore();
    const jobStore = useJobStore();
    const religionStore = useReligionStore();
    const weaponStore = useWeaponStore();

    try {
        await Promise.all([
            buildingStore.fetchBuildings(),
            placeStore.fetchPlaces(),
            sourceStore.fetchSources(),
            tileStore.fetchTiles(),
            districtStore.fetchDistricts(),
            streetStore.fetchStreets(),
            quarterStore.fetchQuarters(),
            jobStore.fetchJobs(),
            religionStore.fetchReligions(),
            weaponStore.fetchWeapons()
        ]);
    } catch (error) {
        console.error("Error initializing stores:", error);
    }
});
