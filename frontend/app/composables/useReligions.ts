export const useReligions = () => {

    /**
     * Clears cached `useAsyncData` entries containing religion data, so pages refetch after create/update/delete.
     * Covers "religions" and "religion-{id}-…".
     */
    const clearReligionData = () =>
        clearNuxtData((key) => key.startsWith("religion"));

    /**
     * `GET` Fetches all religions.
     * Use it with the `useAsyncData` key "religions", which is cleared after create/update/delete.
     * @returns Promise resolving to an array of {@link Religion} entities
     */
    const getReligions = async (): Promise<Religion[]> => {
        return await $fetch<Religion[]>("/api/religions");
    };

    /**
     * `GET` Fetches a single religion by its ID.
     * @param id Unique identifier of the religion
     * @returns Promise resolving to the {@link Religion} entity
     */
    const getReligion = async (id: number): Promise<Religion> => {
        return await $fetch<Religion>(`/api/religions/${id}`);
    };

    /**
     * `POST` Creates a new religion.
     * @param payload Partial religion data to create
     * @returns Promise resolving to the created {@link Religion} entity
     */
    const createReligion = async (payload: Partial<Religion>): Promise<Religion> => {
        const data = await $fetch<Religion>("/api/religions", { method: "POST", body: payload });
        clearReligionData();
        return data;
    };

    /**
     * `PUT` Updates an existing religion.
     * @param id ID of the religion to update
     * @param payload Partial religion data to update
     * @returns Promise resolving to the updated {@link Religion} entity
     */
    const updateReligion = async (id: number, payload: Partial<Religion>): Promise<Religion> => {
        const data = await $fetch<Religion>(`/api/religions/${id}`, { method: "PUT", body: payload });
        clearReligionData();
        return data;
    };

    /**
     * `DELETE` Deletes a religion by its ID.
     * @param id ID of the religion to delete
     */
    const deleteReligion = async (id: number): Promise<void> => {
        await $fetch(`/api/religions/${id}`, { method: "DELETE" });
        clearReligionData();
    };

    /**
     * `GET` Searches religions by name.
     * @param query Search term
     * @returns Promise resolving to an array of {@link ReligionDTO} matching the query
     */
    const searchReligions = async (query: string): Promise<ReligionDTO[]> => {
        return await $fetch<ReligionDTO[]>("/api/religions/search", { query: { query } });
    };

    return {
        getReligions,
        getReligion,
        createReligion,
        updateReligion,
        deleteReligion,
        searchReligions
    };
};
