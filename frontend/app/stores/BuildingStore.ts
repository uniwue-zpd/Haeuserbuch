import type { BuildingDTO, FilterBuilding } from "~/utils/types";
import { defineStore } from "pinia";

export const useBuildingStore = defineStore('building', () => {
    const featureCollection = ref<FeatureCollection | null>(null);
    const cache = ref<Record<number, any>>({});

    /**
     * `GET` all buildings as a `FeatureCollection`
     * @param force Triggers refetching.
     * @returns a `FeatureCollection` containing all buildings.
     */
    async function getBuildings(force: boolean = false): Promise<FeatureCollection> {
        if (!featureCollection.value || force) {
            featureCollection.value = await $fetch<FeatureCollection>('/api/buildings');
            return featureCollection.value;
        } else {
            return featureCollection.value;
        }
    }

    /**
     * `GET` a building with given `id`.
     * @param id `ID` of the building
     * @returns a `Feature` containing the building data. If the building is cached, it returns the cached version instead of making a new API call.
     */
    async function getBuilding(id: number): Promise<Feature> {
        if (cache.value[id]) return cache.value[id];
        const data = await $fetch(`/api/buildings/${id}`);
        cache.value[id] = data;
        return data;
    }

    /**
     * `POST` Sends a request to create new building.
     * @param payload Body as a `Feature`
     */
    async function createBuilding(payload: Partial<Feature>) {
        const data = await $fetch('/api/buildings', {
            method: 'POST',
            body: payload,
        });
        cache.value[data.id] = data;
        await getBuildings(true);
    }

    /**
     * `PUT` Updates an existing place with given `id`.
     * @param id `id` of the building to be updated.
     * @param payload Body as `Feature`
     */
    async function updateBuilding(id: number, payload: Partial<Feature>) {
        cache.value[id] = await $fetch(`/api/buildings/${id}`, {
            method: 'PUT',
            body: payload,
        });
        await getBuildings(true);
    }

    /**
     * `DELETE` Deletes an existing building with given `id`.
     * @param id `id` of the building to be deleted.
     */
    async function deleteBuilding(id: number) {
        await $fetch(`/api/buildings/${id}`, {
            method: 'DELETE',
        });
        delete cache.value[id];
        await getBuildings(true);
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
