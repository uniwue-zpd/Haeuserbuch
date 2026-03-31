/*
    Initializes all available stores
*/
export default defineNuxtPlugin(async (nuxtApp) => {
    const buildingStore = useBuildingStore();
    const placeStore = usePlaceStore();
    const personStore = usePersonStore();
    const sourceStore = useSourceStore();
    const tileStore = useTileStore();
    const districtStore = useDistrictStore();
    const streetStore = useStreetStore();
    const quarterStore = useQuarterStore();
    const citizenshipStore = useCitizenshipStore();
    const occupationStore = useOccupationStore();
    const religionStore = useReligionStore();

    try {
        await Promise.all([
            buildingStore.fetchBuildings(),
            placeStore.fetchPlaces(),
            personStore.fetchPersons(),
            sourceStore.fetchSources(),
            tileStore.fetchTiles(),
            districtStore.fetchDistricts(),
            streetStore.fetchStreets(),
            quarterStore.fetchQuarters(),
            citizenshipStore.fetchCitizenships(),
            occupationStore.fetchOccupations(),
            religionStore.fetchReligions(),
        ]);
    } catch (error) {
        console.error("Error initializing stores:", error);
    }
});
