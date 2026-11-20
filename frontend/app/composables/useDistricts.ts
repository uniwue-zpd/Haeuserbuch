export const useDistricts = () => {

    /**
     * Clears cached `useAsyncData` entries containing district data, so pages refetch after create/update/delete.
     * Covers "districts", "district-{id}-…" and "building-explorer-districts".
     */
    const clearDistrictData = () =>
        clearNuxtData((key) => key.startsWith("district") || key.endsWith("-districts"));

    /**
     * `GET` Fetches all districts.
     * Use it with the `useAsyncData` key "districts", which is cleared after create/update/delete.
     * @returns Promise resolving to an array of {@link District} entities
     */
    const getDistricts = async (): Promise<District[]> => {
        return await $fetch<District[]>("/api/districts");
    };

    /**
     * `GET` Fetches a single district by its ID.
     * @param id Unique identifier of the district
     * @returns Promise resolving to the {@link District} entity
     */
    const getDistrict = async (id: number): Promise<District> => {
        return await $fetch<District>(`/api/districts/${id}`);
    };

    /**
     * `POST` Creates a new district.
     * @param payload Partial district data to create
     * @returns Promise resolving to the created {@link District} entity
     */
    const createDistrict = async (payload: Partial<District>): Promise<District> => {
        const data = await $fetch<District>("/api/districts", { method: "POST", body: payload });
        clearDistrictData();
        return data;
    };

    /**
     * `PUT` Updates an existing district.
     * @param id ID of the district to update
     * @param payload Partial district data to update
     * @returns Promise resolving to the updated {@link District} entity
     */
    const updateDistrict = async (id: number, payload: Partial<District>): Promise<District> => {
        const data = await $fetch<District>(`/api/districts/${id}`, { method: "PUT", body: payload });
        clearDistrictData();
        return data;
    };

    /**
     * `DELETE` Deletes a district by its ID.
     * @param id ID of the district to delete
     */
    const deleteDistrict = async (id: number): Promise<void> => {
        await $fetch(`/api/districts/${id}`, { method: "DELETE" });
        clearDistrictData();
    };

    /**
     * `GET` Searches districts by name.
     * @param query Search term
     * @returns Promise resolving to an array of {@link DistrictDTO} matching the query
     */
    const searchDistricts = async (query: string): Promise<DistrictDTO[]> => {
        return await $fetch<DistrictDTO[]>("/api/districts/search", { query: { query } });
    };

    return {
        getDistricts,
        getDistrict,
        createDistrict,
        updateDistrict,
        deleteDistrict,
        searchDistricts
    };
};
