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
            try {
                const { data } = await useFetch("/api/sources");
                sources.value = data.value as Source[];
            } catch (error) {
                console.error("Error fetching sources:", error);
            }
        }
    }

    // Fetch source by ID
    async function fetchSourceById(id: number) {
        if (!currentSource.value || currentSource.value.id !== id) {
            const cachedSource = sources.value.find(source => source.id === id);
            if (cachedSource) {
                currentSource.value = cachedSource;
            } else {
                try {
                    const { data } = await useFetch<Source>(`/api/sources/${id}`);
                    currentSource.value = data.value as Source;
                } catch (error) {
                    console.error("Error fetching source by ID:", error);
                }
            }
        }
    }

    // Create new source
    async function createSource(payload: Partial<Source>) {
        try {
            const { data } = await useFetch('/api/sources', {
                method: 'POST',
                body: payload,
            });
            sources.value.push(data.value as Source);
            return data.value;
        } catch (error) {
            console.error("Error creating source:", error);
            throw error;
        }
    }

    // Update existing source
    async function updateSource(payload: Partial<Source>, id: number) {
        try {
            if (!sources.value) {
                console.error("Sources data is not loaded");
                return;
            }
            const { data } = await useFetch<Source>(`/api/sources/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = sources.value.findIndex(source => source.id === id);
            if (index !== -1) {
                sources.value[index] = data.value as Source;
            }
            if (currentSource.value?.id === id) {
                currentSource.value = data.value as Source;
            }
            return data.value;
        } catch (error) {
            console.error("Error updating source:", error);
            return;
        }
    }

    // Delete source by ID
    async function deleteSource(id: number) {
        if (!sources.value) {
            console.error("Districts data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/sources/${id}`, {
            method: 'DELETE'
        });
        if (error.value) {
            console.error('Error deleting source:', error.value);
            throw error.value;
        }
        sources.value = sources.value.filter(p => p.id !== id);
        if (currentSource.value?.id === id) {
            currentSource.value = null;
        }
    }

    // Clear current source
    function clearCurrentSource() {
        currentSource.value = null;
    }

    return {
        sources,
        currentSource,
        fetchSources,
        fetchSourceById,
        createSource,
        updateSource,
        deleteSource,
        clearCurrentSource
    }
});
