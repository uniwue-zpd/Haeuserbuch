import { defineStore } from "pinia";
import type { Feature, FeatureCollection } from "~/utils/GeoJsonTypes";

export const usePlaceStore = defineStore("place", () => {
    const featureCollection = ref<FeatureCollection | null>(null);
    const cache = ref<Record<number, any>>({});

    /**
     * `GET` all places stored in the database as `geoJSON` `FeatureCollelction`
     * and cache it in store.
     * @param force Whether to force a refetch. Defaults to `false`.
     * @returns The places as a `geoJSON` `FeatureCollection`
     */
    async function getPlaces(force: boolean = false) {
        if (!featureCollection.value || force) {
            featureCollection.value = await $fetch<FeatureCollection>('/api/places');
            return featureCollection.value;
        } else {
            return featureCollection.value;
        }
    }

    /**
     * `GET` a place with given `id` and cache it in store.
     * @param id ID of the place to be fetched
     * @returns The place as a `geoJSON` `Feature`
     */
    async function getPlace(id: number): Promise<Feature> {
        if (cache.value[id]) return cache.value[id];
        const data = await $fetch(`/api/places/${id}`);
        cache.value[id] = data;
        return data;
    }

    /**
     * `POST` Create new place in the database and cache it in store.
     * @param payload Body of the request as `geoJSON` feature.
     */
    async function createPlace(payload: Partial<Feature>) {
        const data = await $fetch(`/api/places`, {
            method: "POST",
            body: payload
        });
        cache.value[data.id] = data;
        await getPlaces(true);
    }

    /**
     * `PUT` Update an existing place and update its cache value in store.
     * @param id ID of the given item.
     * @param payload Body of the request as `geoJSON` feature with updated values.
     */
    async function updatePlace(id: number, payload: Partial<Feature>) {
        cache.value[id] = await $fetch(`/api/places/${id}`, {
            method: "PUT",
            body: payload
        });
        await getPlaces(true);
    }

    /**
     * `DELETE` Delete an existing place and remove it from the store.
     * @param id ID of the place to be deleted.
     */
    async function deletePlace(id: number) {
        await $fetch(`/api/places/${id}`, { method: "DELETE" });
        delete cache.value[id];
        await getPlaces(true);
    }

    /**
     * Fetches an array of places based on a search query.
     * @param query Query to be used for searching places.
     * @returns An array of `PlaceDTO` matching the search query.
     */
    async function searchPlaces(query: string): Promise<PlaceDTO[]> {
        try {
            return await $fetch<PlaceDTO[]>('/api/places/search', { query: { query: query } });
        } catch (err) {
            console.error('Error searching places', err);
            return [];
        }
    }

    return {
        getPlaces,
        getPlace,
        createPlace,
        updatePlace,
        deletePlace,
        searchPlaces
    }
});
