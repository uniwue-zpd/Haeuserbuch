export const useReligionStore = defineStore("religion", () => {
    // State
    const cache = ref<Record<number, Religion>>({});
    const loading = ref(false);

    // Actions

    /**
     * `GET` Fetch all religions from backend.
     * @returns Promise resolving to an array of Religion entities
     */
    async function fetchReligions(): Promise<Religion[]> {
        loading.value = true;
        try {
            return await $fetch<Religion[]>("/api/religions");
        } finally {
            loading.value = false;
        }
    }

    /**
     * `GET` Fetch a single religion by ID.
     * @param id Unique identifier of the religion
     * @returns Religion entity or null if request fails
     */
    async function fetchReligionById(id: number): Promise<Religion | null> {
        if (cache.value[id]) return cache.value[id];
        try {
            const data = await $fetch<Religion>(`/api/religions/${id}`);
            cache.value[id] = data;
            return data;
        } catch (error) {
            console.error(`Error fetching religion with ID ${id}:`, error);
            return null;
        }
    }

    /**
     * `POST` Create a new religion entry.
     * @param payload Partial religion data
     * @returns Created Religion entity
     */
    async function createReligion(payload: Partial<Religion>): Promise<Religion> {
        const data = await $fetch<Religion>("/api/religions", {
            method: "POST",
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * `PUT` Update an existing religion.
     * @param id ID of the religion to update
     * @param payload Partial update data
     * @returns Updated Religion entity
     */
    async function updateReligion(id: number, payload: Partial<Religion>): Promise<Religion> {
        const data = await $fetch<Religion>(`/api/religions/${id}`, {
            method: "PUT",
            body: payload
        });
        cache.value[id] = data;
        return data;
    }

    /**
     * `DELETE` Remove a religion by ID.
     * @param id ID of the religion
     */
    async function deleteReligion(id: number): Promise<void> {
        try {
            await $fetch(`/api/religions/${id}`, { method: "DELETE" });
            delete cache.value[id];
        } catch (error) {
            console.error(`Error deleting religion with ID ${id}:`, error);
        }
    }

    /**
     * `GET` Search religions by query string.
     * @param query Search term
     * @returns Array of ReligionDTO results
     */
    async function searchReligions(query: string): Promise<ReligionDTO[]> {
        try {
            return await $fetch<ReligionDTO[]>("/api/religions/search", {
                params: { query }
            });
        } catch (error) {
            console.error("Error searching religions:", error);
            return [];
        }
    }

    return {
        cache,
        loading,
        fetchReligions,
        fetchReligionById,
        createReligion,
        updateReligion,
        deleteReligion,
        searchReligions
    };
});
