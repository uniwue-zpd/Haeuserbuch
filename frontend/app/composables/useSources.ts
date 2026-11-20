export const useSources = () => {

    /**
     * Clears cached `useAsyncData` entries containing source data, so pages refetch after create/update/delete.
     * Covers "sources" and "source-{id}-…".
     */
    const clearSourceData = () =>
        clearNuxtData((key) => key.startsWith("source"));

    /**
     * `GET` Fetches all sources.
     * Use it with the `useAsyncData` key "sources", which is cleared after create/update/delete.
     * @returns Promise resolving to an array of {@link Source} entities
     */
    const getSources = async (): Promise<Source[]> => {
        return await $fetch<Source[]>("/api/sources");
    };

    /**
     * `GET` Fetches a single source by its ID.
     * @param id Unique identifier of the source
     * @returns Promise resolving to the {@link Source} entity
     */
    const getSource = async (id: number): Promise<Source> => {
        return await $fetch<Source>(`/api/sources/${id}`);
    };

    /**
     * `POST` Creates a new source.
     * @param payload Partial source data to create
     * @returns Promise resolving to the created {@link Source} entity
     */
    const createSource = async (payload: Partial<Source>): Promise<Source> => {
        const data = await $fetch<Source>("/api/sources", { method: "POST", body: payload });
        clearSourceData();
        return data;
    };

    /**
     * `PUT` Updates an existing source.
     * @param id ID of the source to update
     * @param payload Partial source data to update
     * @returns Promise resolving to the updated {@link Source} entity
     */
    const updateSource = async (id: number, payload: Partial<Source>): Promise<Source> => {
        const data = await $fetch<Source>(`/api/sources/${id}`, { method: "PUT", body: payload });
        clearSourceData();
        return data;
    };

    /**
     * `DELETE` Deletes a source by its ID.
     * @param id ID of the source to delete
     */
    const deleteSource = async (id: number): Promise<void> => {
        await $fetch(`/api/sources/${id}`, { method: "DELETE" });
        clearSourceData();
    };

    /**
     * `GET` Searches sources by title.
     * @param query Search term
     * @returns Promise resolving to an array of {@link SourceDTO} matching the query
     */
    const searchSources = async (query: string): Promise<SourceDTO[]> => {
        return await $fetch<SourceDTO[]>("/api/sources/search", { query: { query } });
    };

    return {
        getSources,
        getSource,
        createSource,
        updateSource,
        deleteSource,
        searchSources
    };
};
