import { ref } from "vue";
import type { Street, StreetDTO } from "~/utils/types";

export const useStreetStore = defineStore("street", () => {
    // State
    const cache = ref<Record<number, Street>>({});
    const loading = ref(false);

    // Actions

    /**
     * `GET` Fetch all streets from backend.
     * @returns Promise resolving to an array of Street entities
     */
    async function fetchStreets(): Promise<Street[]> {
        loading.value = true;
        try {
            return await $fetch<Street[]>("/api/streets");
        } finally {
            loading.value = false;
        }
    }

    /**
     * `GET` Fetch a single street by ID.
     * @param id Unique identifier of the street
     * @returns Street entity or null if request fails
     */
    async function fetchStreetById(id: number): Promise<Street | null> {
        if (cache.value[id]) return cache.value[id];
        try {
            const data = await $fetch<Street>(`/api/streets/${id}`);
            cache.value[id] = data;
            return data;
        } catch (error) {
            console.error(`Error fetching street with ID ${id}:`, error);
            return null;
        }
    }

    /**
     * `POST` Create a new street entry.
     * @param payload Partial street data
     * @returns Newly created Street entity
     */
    async function createStreet(payload: Partial<Street>): Promise<Street> {
        const data = await $fetch<Street>("/api/streets", {
            method: "POST",
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * `PUT` Update an existing street.
     * @param id ID of street to update
     * @param payload Partial update data
     * @returns Updated Street entity
     */
    async function updateStreet(
        id: number,
        payload: Partial<Street>
    ): Promise<Street> {
        const data = await $fetch<Street>(`/api/streets/${id}`, {
            method: "PUT",
            body: payload
        });
        cache.value[id] = data;
        return data;
    }

    /**
     * `DELETE` Remove a street by ID.
     * @param id ID of street to delete
     */
    async function deleteStreet(id: number): Promise<void> {
        try {
            await $fetch(`/api/streets/${id}`, {
                method: "DELETE"
            });
            delete cache.value[id];
        } catch (error) {
            console.error(`Error deleting street with ID ${id}:`, error);
        }
    }

    /**
     * `GET` Search streets using a query string.
     * @param query Search term
     * @returns Array of StreetDTO matches
     */
    async function searchStreets(query: string): Promise<StreetDTO[]> {
        try {
            return await $fetch<StreetDTO[]>("/api/streets/search", {
                params: { query }
            });
        } catch (error) {
            console.error("Error searching streets:", error);
            return [];
        }
    }

    return {
        cache,
        loading,
        fetchStreets,
        fetchStreetById,
        createStreet,
        updateStreet,
        deleteStreet,
        searchStreets
    };
});
