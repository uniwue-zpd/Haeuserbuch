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
                const response = await apiClient.get<Quarter[]>("/quarters");
                quarters.value = response.data;
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
                    const response = await apiClient.get<Quarter>(`/quarters/${id}`);
                    current_quarter.value = response.data;
                } catch (error) {
                    console.error("Error fetching quarter by ID:", error);
                }
            }
        }
    }

        // Create new quarter
    async function createQuarter(payload: Partial<Quarter>) {
        try {
            const response = await apiClient.post<Quarter>('/quarters', payload);
            quarters.value.push(response.data);
            return response.data;
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
            const response = await apiClient.put<Quarter>(`/quarters/${id}`, payload);
            const index = quarters.value.findIndex(q => q.id === id);
            if (index !== -1) {
                quarters.value[index] = response.data;
            }
            if (current_quarter.value && current_quarter.value.id === id) {
                current_quarter.value = response.data;
            }
            return response.data;
        } catch (error) {
            console.error("Error updating quarter:", error);
            throw error;
        }
    }

        // Delete quarter
    async function deleteQuarter(id: number) {
        try {
            await apiClient.delete(`/quarters/${id}`);
            quarters.value = quarters.value.filter(q => q.id !== id);
            if (current_quarter.value && current_quarter.value.id === id) {
                current_quarter.value = null;
            }
        } catch (error) {
            console.error("Error deleting quarter:", error);
            throw error;
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
