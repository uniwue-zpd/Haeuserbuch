import type {BuildingDTO, FilterBuilding} from "~/utils/types";

export const useBuildingStore = defineStore('buildings', () => {
    const featureCollection = ref<FeatureCollection | null>(null);
    const cache = ref<Record<number, any>>({});

    async function getBuildings(force: boolean = false): Promise<FeatureCollection> {
        if (!featureCollection.value || force) {
            featureCollection.value = await $fetch<FeatureCollection>('/api/buildings');
            return featureCollection.value;
        } else {
            return featureCollection.value;
        }
    }

    async function getBuilding(id: number): Promise<Feature> {
        if (cache.value[id]) return cache.value[id];
        const data = await $fetch(`/api/buildings/${id}`);
        cache.value[id] = data;
        return data;
    }

    async function createBuilding(payload: Partial<Feature>) {
        const data = await $fetch('/api/buildings', {
            method: 'POST',
            body: payload,
        });
        await getBuildings(true);
        cache.value[data.id] = data;
    }

    async function updateBuilding(id: number, payload: Partial<Feature>) {
        cache.value[id] = await $fetch(`/api/buildings/${id}`, {
            method: 'PUT',
            body: payload,
        });
        await getBuildings(true);
    }

    async function deleteBuilding(id: number) {
        await $fetch(`/api/buildings/${id}`, {
            method: 'DELETE',
        });
        await getBuildings(true);
        delete cache.value[id];
    }

    /**
     * Fetches an array of buildings based on a search query.
     * @param query Query to be used for searching buildings.
     * @returns An array of `BuildingDTO` matching the search query.
     */
    async function searchBuildings(query: string): Promise<BuildingDTO[]> {
        return await $fetch('/api/buildings/search', {query: {query: query}});
    }

    async function filterBuildings(params: FilterBuilding): Promise<BuildingDTO[]> {
        return await $fetch('/api/buildings/filter', {query: params});
    }

    return {
        getBuildings,
        getBuilding,
        createBuilding,
        updateBuilding,
        deleteBuilding,
        searchBuildings,
        filterBuildings
    }
});
