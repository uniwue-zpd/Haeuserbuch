import {defineStore} from "pinia";
import {computed, ref} from "vue";

export const useSourceStore = defineStore("source", () => {
    // State
    const sources = ref<Source[]>([]);
    const currentSource = ref<Source | null>(null);

    // Getters
    const isLoaded = computed(() => sources.value.length > 0);

    // Actions
    // Fetch sources from the API
    async function fetchSources() {
        if (!isLoaded.value) {
            const { data, error } = await useFetch("/api/sources");
            if (error.value) {
                console.error("Error fetching sources:", error.value);
                return;
            }
            sources.value = data.value as Source[];
        }
    }

    // Fetch source by ID
    async function fetchSourceById(id: number) {
        if (!currentSource.value || currentSource.value.id !== id) {
            const cachedSource = sources.value.find(source => source.id === id);
            if (cachedSource) {
                currentSource.value = cachedSource;
            } else {
                const { data, error } = await useFetch<Source>(`/api/sources/${id}`);
                if (error.value) {
                    console.error(`Error fetching source by ID: ${ id }`, error.value);
                    return;
                }
                currentSource.value = data.value as Source;
            }
        }
    }

    // Create new source
    async function createSource(payload: Partial<Source>) {
        const { data, error } = await useFetch('/api/sources', {
            method: 'POST',
            body: payload,
        });
        if (error.value) {
            console.error("Error creating source:", error.value);
            return;
        }
        sources.value.push(data.value as Source);
        return data.value;
    }

    // Update existing source
    async function updateSource(payload: Partial<Source>, id: number) {
        if (sources.value.length === 0) {
            console.error("Sources data is not loaded");
            return;
        }
        const { data, error } = await useFetch<Source>(`/api/sources/${id}`, {
            method: 'PUT',
            body: payload,
        });
        if (error.value) {
            console.error("Error updating source:", error.value);
            return;
        }
        const updatedSource = data.value as Source;
        const index = sources.value.findIndex(source => source.id === id);
        if (index !== -1) sources.value[index] = data.value as Source;
        if (currentSource.value?.id === id) currentSource.value = data.value as Source;
        return updatedSource;
    }

    // Delete source by ID
    async function deleteSource(id: number) {
        if (!sources.value) {
            console.error("Districts data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/sources/${id}`, { method: 'DELETE' });
        if (error.value) {
            console.error('Error deleting source:', error.value);
        }
        sources.value = sources.value.filter(p => p.id !== id);
        if (currentSource.value?.id === id) currentSource.value = null;
    }

    // Clear current source
    function clearCurrentSource() {
        currentSource.value = null;
    }

    /**
     * Allows searching for sources
     * @param query The search query string
     * @returns A promise that resolves to an array of SourceDTOs matching the search query
     */
    async function searchSources(query: string): Promise<SourceDTO[]> {
        try {
            return await $fetch<SourceDTO[]>(`/api/sources/search`, { params: { query: query } });
        } catch (error) {
            console.error(error);
            return [];
        }
    }

    return {
        sources,
        currentSource,
        fetchSources,
        fetchSourceById,
        createSource,
        updateSource,
        deleteSource,
        clearCurrentSource,
        searchSources,
    }
});
