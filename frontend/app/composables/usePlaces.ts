export const usePlaces = () => {

    /**
     * Clears cached `useAsyncData` entries containing place data, so pages refetch after create/update/delete.
     * Covers "places", "place-{id}…" and the person map entries ("person-{id}-origin-places", "…-emigration-place").
     */
    const clearPlaceData = () =>
        clearNuxtData((key) => key.startsWith("place") || key.includes("-place"));

    /**
     * `GET` all places as a `FeatureCollection`.
     * Use it with the `useAsyncData` key "places", which is cleared after create/update/delete.
     */
    const getPlaces = async (): Promise<FeatureCollection> => {
        return await $fetch<FeatureCollection>("/api/places");
    };

    const getPlace = async (id: number): Promise<Feature> => {
        return await $fetch<Feature>(`/api/places/${id}`);
    };

    const createPlace = async (payload: Partial<Feature>): Promise<Feature> => {
        const data = await $fetch<Feature>("/api/places", { method: "POST", body: payload });
        clearPlaceData();
        return data;
    };

    const updatePlace = async (id: number, payload: Partial<Feature>): Promise<Feature> => {
        const data = await $fetch<Feature>(`/api/places/${id}`, { method: "PUT", body: payload });
        clearPlaceData();
        return data;
    };

    const deletePlace = async (id: number): Promise<void> => {
        await $fetch(`/api/places/${id}`, { method: "DELETE" });
        clearPlaceData();
    };

    /**
     * Searches places by name and alternative spellings.
     */
    const searchPlaces = async (query: string): Promise<PlaceDTO[]> => {
        return await $fetch<PlaceDTO[]>("/api/places/search", { query: { query } });
    };

    /**
     * `GET` a page of the place register: places with their related persons or citizenship entries,
     * sorted by name and optionally filtered by real or alternative place name.
     * Use it with the `useAsyncData` key "place-register-{type}", which is cleared after create/update/delete.
     * @param params Register type, name filter and paging.
     * @returns A page of places with their entries.
     */
    const getPlaceRegister = async <T>(params: FilterPlaceRegister): Promise<Page<PlaceRegisterEntry<T>>> => {
        return await $fetch<Page<PlaceRegisterEntry<T>>>("/api/places/register", { query: params });
    };

    return {
        getPlaceRegister,
        getPlaces,
        getPlace,
        createPlace,
        updatePlace,
        deletePlace,
        searchPlaces
    };
};
