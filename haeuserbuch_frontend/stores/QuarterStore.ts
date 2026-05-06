import { ref } from "vue";
import type { Quarter, QuarterDTO } from "~/utils/types";

export const useQuarterStore = defineStore("quarter", () => {
    // State
    const cache = ref<Record<number, Quarter>>({});
    const loading = ref(false);

    /**
     * `GET` Fetches all quarters.
     */
    async function fetchQuarters(): Promise<Quarter[]> {
        loading.value = true;
        try {
            return await $fetch<Quarter[]>("/api/quarters");
        } finally {
            loading.value = false;
        }
    }

    /**
     * `GET` Fetch a single quarter by ID.
     * Uses cache if available.
     */
    async function fetchQuarterById(id: number): Promise<Quarter | null> {
        if (cache.value[id]) return cache.value[id];
        try {
            const data = await $fetch<Quarter>(`/api/quarters/${id}`);
            cache.value[id] = data;
            return data;
        } catch (error) {
            console.error(`Error fetching quarter ${id}:`, error);
            return null;
        }
    }

    /**
     * `POST` Create a new quarter.
     * Adds it to cache.
     */
    async function createQuarter(payload: Partial<Quarter>): Promise<Quarter> {
        const data = await $fetch<Quarter>("/api/quarters", {
            method: "POST",
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * `PUT` Update a quarter.
     * Updates cache entry.
     */
    async function updateQuarter(id: number, payload: Partial<Quarter>): Promise<Quarter> {
        const data = await $fetch<Quarter>(`/api/quarters/${id}`, {
            method: "PUT",
            body: payload
        });
        cache.value[id] = data;
        return data;
    }

    /**
     * `DELETE` Remove a quarter.
     * Deletes it from cache.
     */
    async function deleteQuarter(id: number): Promise<void> {
        try {
            await $fetch(`/api/quarters/${id}`, { method: "DELETE" });
            delete cache.value[id];
        } catch (error) {
            console.error(`Error deleting quarter ${id}:`, error);
        }
    }

    /**
     * `GET` Search quarters by query string.
     * Returns lightweight DTOs.
     */
    async function searchQuarters(query: string): Promise<QuarterDTO[]> {
        try {
            return await $fetch<QuarterDTO[]>("/api/quarters/search", {
                params: { query }
            });
        } catch (error) {
            console.error("Error searching quarters:", error);
            return [];
        }
    }

    return {
        cache,
        loading,
        fetchQuarters,
        fetchQuarterById,
        createQuarter,
        updateQuarter,
        deleteQuarter,
        searchQuarters
    };
});
