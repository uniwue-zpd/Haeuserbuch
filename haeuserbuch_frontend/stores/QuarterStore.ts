import { ref, computed } from "vue";
import type { Quarter } from "~/utils/types";
import apiClient from "~/service/api";

export const useQuarterStore = defineStore("quarter", () => {
    // State
    const quarters = ref<Quarter[]>([]);
    const current_quarter = ref<Quarter | null>(null);

    // Getters
    const isLoaded = computed(() => quarters.value.length > 0);

    // Actions
        // Fetch quarters from the API
    async function fetchQuarters() {
        if (!isLoaded.value) {
            try {
                const { data } = await useFetch("/api/quarters");
                quarters.value = data.value as Quarter[];
            } catch (error) {
                console.error("Error fetching quarters:", error);
            }
        }
    }

        // Fetch quarter by ID
    async function fetchQuarterById(id: number) {
        if (!current_quarter.value || current_quarter.value.id !== id) {
            const cachedQuarter = quarters.value.find(quarter => quarter.id === id);
            if (cachedQuarter) {
                current_quarter.value = cachedQuarter;
            } else {
                try {
                    const { data } = await useFetch(`/api/quarters/${id}`);
                    current_quarter.value = data.value as Quarter;
                } catch (error) {
                    console.error("Error fetching quarter by ID:", error);
                }
            }
        }
    }

        // Create new quarter
    async function createQuarter(payload: Partial<Quarter>) {
        try {
            const { data } = await useFetch('/api/quarters', {
                method: 'POST',
                body: payload,
            });
            quarters.value.push(data.value as Quarter);
            return data.value;
        } catch (error) {
            console.error("Error creating quarter:", error);
            throw error;
        }
    }

        // Update existing quarter
    async function updateQuarter(payload: Partial<Quarter>, id: number) {
        try {
            if (!quarters.value.length) {
                console.error("Quarters data is not loaded");
                return;
            }
            const { data } = await useFetch<Quarter>(`/api/quarters/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = quarters.value.findIndex(q => q.id === id);
            if (index !== -1) {
                quarters.value[index] = data.value as Quarter;
            }
            if (current_quarter.value && current_quarter.value.id === id) {
                current_quarter.value = data.value as Quarter;
            }
            return data.value;
        } catch (error) {
            console.error("Error updating quarter:", error);
            throw error;
        }
    }

        // Delete quarter
    async function deleteQuarter(id: number) {
        if (!quarters.value) {
            console.error("Quarters data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/quarters/${id}`, {
            method: 'DELETE'
        });
        if (error.value) {
            console.error('Error deleting quarter:', error.value);
            throw error.value;
        }
        quarters.value = quarters.value.filter(p => p.id !== id);
        if (current_quarter.value?.id === id) {
            current_quarter.value = null;
        }
    }

        // Clear current quarter
    function clearCurrentQuarter() {
        current_quarter.value = null;
    }

    return {
        quarters,
        current_quarter,
        isLoaded,
        fetchQuarters,
        fetchQuarterById,
        createQuarter,
        updateQuarter,
        deleteQuarter,
        clearCurrentQuarter
    }
});
