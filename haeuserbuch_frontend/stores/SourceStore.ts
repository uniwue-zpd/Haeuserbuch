import { ref } from "vue";
import { defineStore } from "pinia";

export const useSourceStore = defineStore("source", () => {
    // State
    const cache = ref<Record<number, Source>>({});
    const loading = ref(false);

    // Actions

    /**
     * `GET` Fetch all sources from backend.
     * @returns Promise resolving to an array of Source entities
     */
    async function fetchSources(): Promise<Source[]> {
        loading.value = true;

        try {
            return await $fetch<Source[]>("/api/sources");
        } finally {
            loading.value = false;
        }
    }

    /**
     * `GET` Fetch a single source by ID.
     * @param id Unique identifier of the source
     * @returns Source entity or null if request fails
     */
    async function fetchSourceById(id: number): Promise<Source | null> {
        if (cache.value[id]) return cache.value[id];
        try {
            const data = await $fetch<Source>(`/api/sources/${id}`);
            cache.value[id] = data;
            return data;
        } catch (error) {
            console.error(`Error fetching source with ID ${id}:`, error);
            return null;
        }
    }

    /**
     * `POST` Create a new source entry.
     * @param payload Partial source data
     * @returns Newly created Source entity
     */
    async function createSource(payload: Partial<Source>): Promise<Source> {
        const data = await $fetch<Source>("/api/sources", {
            method: "POST",
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * `PUT` Update an existing source.
     * @param id ID of source to update
     * @param payload Partial update data
     * @returns Updated Source entity
     */
    async function updateSource(id: number, payload: Partial<Source>): Promise<Source> {
        const data = await $fetch<Source>(`/api/sources/${id}`, {
            method: "PUT",
            body: payload
        });
        cache.value[id] = data;
        return data;
    }

    /**
     * `DELETE` Remove a source by ID.
     * @param id ID of source to delete
     */
    async function deleteSource(id: number): Promise<void> {
        try {
            await $fetch(`/api/sources/${id}`, {
                method: "DELETE"
            });
            delete cache.value[id];
        } catch (error) {
            console.error(`Error deleting source with ID ${id}:`, error);
        }
    }

    /**
     * `GET` Search sources using a query string.
     * @param query Search term
     * @returns Array of SourceDTO matches
     */
    async function searchSources(query: string): Promise<SourceDTO[]> {
        try {
            return await $fetch<SourceDTO[]>("/api/sources/search", {
                params: { query }
            });
        } catch (error) {
            console.error("Error searching sources:", error);
            return [];
        }
    }

    return {
        cache,
        loading,
        fetchSources,
        fetchSourceById,
        createSource,
        updateSource,
        deleteSource,
        searchSources
    };
});
