export const useQuarters = () => {

    /**
     * Clears cached `useAsyncData` entries containing quarter data, so pages refetch after create/update/delete.
     * Covers "quarters", "quarter-{id}-…" and "building-explorer-quarters".
     */
    const clearQuarterData = () =>
        clearNuxtData((key) => key.startsWith("quarter") || key.endsWith("-quarters"));

    /**
     * `GET` Fetches all quarters.
     * Use it with the `useAsyncData` key "quarters", which is cleared after create/update/delete.
     * @returns Promise resolving to an array of {@link Quarter} entities
     */
    const getQuarters = async (): Promise<Quarter[]> => {
        return await $fetch<Quarter[]>("/api/quarters");
    };

    /**
     * `GET` Fetches a single quarter by its ID.
     * @param id Unique identifier of the quarter
     * @returns Promise resolving to the {@link Quarter} entity
     */
    const getQuarter = async (id: number): Promise<Quarter> => {
        return await $fetch<Quarter>(`/api/quarters/${id}`);
    };

    /**
     * `POST` Creates a new quarter.
     * @param payload Partial quarter data to create
     * @returns Promise resolving to the created {@link Quarter} entity
     */
    const createQuarter = async (payload: Partial<Quarter>): Promise<Quarter> => {
        const data = await $fetch<Quarter>("/api/quarters", { method: "POST", body: payload });
        clearQuarterData();
        return data;
    };

    /**
     * `PUT` Updates an existing quarter.
     * @param id ID of the quarter to update
     * @param payload Partial quarter data to update
     * @returns Promise resolving to the updated {@link Quarter} entity
     */
    const updateQuarter = async (id: number, payload: Partial<Quarter>): Promise<Quarter> => {
        const data = await $fetch<Quarter>(`/api/quarters/${id}`, { method: "PUT", body: payload });
        clearQuarterData();
        return data;
    };

    /**
     * `DELETE` Deletes a quarter by its ID.
     * @param id ID of the quarter to delete
     */
    const deleteQuarter = async (id: number): Promise<void> => {
        await $fetch(`/api/quarters/${id}`, { method: "DELETE" });
        clearQuarterData();
    };

    /**
     * `GET` Searches quarters by name.
     * @param query Search term
     * @returns Promise resolving to an array of {@link QuarterDTO} matching the query
     */
    const searchQuarters = async (query: string): Promise<QuarterDTO[]> => {
        return await $fetch<QuarterDTO[]>("/api/quarters/search", { query: { query } });
    };

    return {
        getQuarters,
        getQuarter,
        createQuarter,
        updateQuarter,
        deleteQuarter,
        searchQuarters
    };
};
