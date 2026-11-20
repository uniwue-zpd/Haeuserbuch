export const useStreets = () => {

    /**
     * Clears cached `useAsyncData` entries containing street data, so pages refetch after create/update/delete.
     * Covers "streets" and "street-{id}-…".
     */
    const clearStreetData = () =>
        clearNuxtData((key) => key.startsWith("street"));

    /**
     * `GET` Fetches all streets.
     * Use it with the `useAsyncData` key "streets", which is cleared after create/update/delete.
     * @returns Promise resolving to an array of {@link Street} entities
     */
    const getStreets = async (): Promise<Street[]> => {
        return await $fetch<Street[]>("/api/streets");
    };

    /**
     * `GET` Fetches a single street by its ID.
     * @param id Unique identifier of the street
     * @returns Promise resolving to the {@link Street} entity
     */
    const getStreet = async (id: number): Promise<Street> => {
        return await $fetch<Street>(`/api/streets/${id}`);
    };

    /**
     * `POST` Creates a new street.
     * @param payload Partial street data to create
     * @returns Promise resolving to the created {@link Street} entity
     */
    const createStreet = async (payload: Partial<Street>): Promise<Street> => {
        const data = await $fetch<Street>("/api/streets", { method: "POST", body: payload });
        clearStreetData();
        return data;
    };

    /**
     * `PUT` Updates an existing street.
     * @param id ID of the street to update
     * @param payload Partial street data to update
     * @returns Promise resolving to the updated {@link Street} entity
     */
    const updateStreet = async (id: number, payload: Partial<Street>): Promise<Street> => {
        const data = await $fetch<Street>(`/api/streets/${id}`, { method: "PUT", body: payload });
        clearStreetData();
        return data;
    };

    /**
     * `DELETE` Deletes a street by its ID.
     * @param id ID of the street to delete
     */
    const deleteStreet = async (id: number): Promise<void> => {
        await $fetch(`/api/streets/${id}`, { method: "DELETE" });
        clearStreetData();
    };

    /**
     * `GET` Searches streets by name and alternative spellings.
     * @param query Search term
     * @returns Promise resolving to an array of {@link StreetDTO} matching the query
     */
    const searchStreets = async (query: string): Promise<StreetDTO[]> => {
        return await $fetch<StreetDTO[]>("/api/streets/search", { query: { query } });
    };

    return {
        getStreets,
        getStreet,
        createStreet,
        updateStreet,
        deleteStreet,
        searchStreets
    };
};
