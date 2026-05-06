import { ref } from "vue";
import type { District, DistrictDTO } from "~/utils/types";

export const useDistrictStore = defineStore("district", () => {
    // State
    const cache = ref<Record<number, District>>({});
    const loading = ref(false);

    // Actions

    /**
     * `GET` Fetches all districts from the backend.
     * This endpoint loads the complete dataset and should only be used
     * @returns Promise resolving to an array of District entities.
     */
    async function fetchDistricts(): Promise<District[]> {
        loading.value = true;
        try {
            return await $fetch<District[]>("/api/districts");
        } finally {
            loading.value = false;
        }
    }

    /**
     * `GET` Fetches a single district by its ID.
     * @param id Unique identifier of the district
     * @returns The District object or null if request fails
     */
    async function fetchDistrictById(id: number): Promise<District | null> {
        if (cache.value[id]) {
            return cache.value[id];
        }
        try {
            const data = await $fetch<District>(`/api/districts/${id}`);
            cache.value[id] = data;
            return data;
        } catch (error) {
            console.error(`Error fetching district with ID ${id}:`, error)
            return null;
        }
    }

    /**
     * `POST` Creates a new district entry;
     * @param payload Partial district data to create;
     * @returns The created District entity;
     */
    async function createDistrict(payload: Partial<District>): Promise<District> {
        const data = await $fetch<District>("/api/districts", {
            method: "POST",
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * `PUT` Updates an existing district.
     * @param id ID of the district to update
     * @param payload Partial data to update
     * @returns The updated District entity
     */
    async function updateDistrict(id: number, payload: Partial<District>): Promise<District> {
        const data = await $fetch<District>(`/api/districts/${id}`, {
            method: "PUT",
            body: payload
        });
        cache.value[id] = data;
        return data;
    }

    /**
     * `DELETE` Deletes a district by ID.
     * @param id ID of the district to delete
     */
    async function deleteDistrict(id: number): Promise<void> {
        try {
            await $fetch(`/api/districts/${id}`, { method: "DELETE" });
            delete cache.value[id];
        } catch (error) {
            console.error(`Error deleting district with ID ${id}:`, error);
        }
    }

    /**
     * `GET` Searches districts based on a query string.
     * @param query Search term
     * @returns Array of DistrictDTO results matching the query
     */
    async function searchDistricts(query: string): Promise<DistrictDTO[]> {
        try {
            return await $fetch<DistrictDTO[]>("/api/districts/search", { params: { query } });
        } catch (error) {
            console.error("Error searching districts:", error);
            return [];
        }
    }

    return {
        cache,
        loading,
        fetchDistricts,
        fetchDistrictById,
        createDistrict,
        updateDistrict,
        deleteDistrict,
        searchDistricts
    };
})
