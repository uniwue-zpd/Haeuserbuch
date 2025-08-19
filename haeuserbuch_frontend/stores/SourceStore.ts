import {defineStore} from "pinia";
import {computed, ref} from "vue";
import apiClient from "~/service/api";

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
                const response = await apiClient.get<Source[]>("/sources");
                sources.value = response.data;
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
                    const response = await apiClient.get<Source>(`/sources/${id}`);
                    currentSource.value = response.data;
                } catch (error) {
                    console.error("Error fetching source by ID:", error);
                }
            }
        }
    }

    // Create new source
    async function createSource(payload: Partial<Source>) {
        try {
            const response = await apiClient.post('/sources', payload);
            sources.value.push(response.data);
            return response.data;
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
            const response = await apiClient.put(`/sources/${id}`, payload);
            const index = sources.value.findIndex(source => source.id === id);
            if (index !== -1) {
                sources.value[index] = response.data;
            }
            if (currentSource.value?.id === id) {
                currentSource.value = response.data;
            }
            return response.data;
        } catch (error) {
            console.error("Error updating source:", error);
            return;
        }
    }

    // Delete source by ID
    async function deleteSource(id: number) {
        try {
            if (!sources.value) {
                console.error("Sources data is not loaded");
                return;
            }
            await apiClient.delete(`/sources/${id}`);
            sources.value = sources.value.filter(p => p.id !== id);
            if (currentSource.value?.id === id) {
                currentSource.value = null;
            }
        } catch (error) {
            console.log('Error deleting source:', error);
            throw error;
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
