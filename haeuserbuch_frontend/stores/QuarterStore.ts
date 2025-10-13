import { ref, computed } from "vue";
import type { Quarter } from "~/utils/types";

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
            const { data, error } = await useFetch("/api/quarters");
            if (error.value) {
                console.error("Error fetching quarters:", error.value);
                return;
            }
            quarters.value = data.value as Quarter[];
        }
    }

        // Fetch quarter by ID
    async function fetchQuarterById(id: number) {
        if (!current_quarter.value || current_quarter.value.id !== id) {
            const cachedQuarter = quarters.value.find(quarter => quarter.id === id);
            if (cachedQuarter) {
                current_quarter.value = cachedQuarter;
            } else {
                const { data, error } = await useFetch(`/api/quarters/${id}`);
                if (error.value) {
                    console.error(`Error fetching quarter by ID: ${ id }`, error.value);
                    return;
                }
                current_quarter.value = data.value as Quarter;
            }
        }
    }

        // Create new quarter
    async function createQuarter(payload: Partial<Quarter>) {
        const { data, error } = await useFetch('/api/quarters', {
            method: 'POST',
            body: payload,
        });
        if (error.value) {
            console.error("Error creating quarter:", error.value);
            return;
        }
        quarters.value.push(data.value as Quarter);
        return data.value;
    }

        // Update existing quarter
    async function updateQuarter(payload: Partial<Quarter>, id: number) {
        if (quarters.value.length === 0) {
            console.error("Quarters data is not loaded");
            return;
        }
        const { data, error } = await useFetch<Quarter>(`/api/quarters/${id}`, {
            method: 'PUT',
            body: payload,
        });
        if (error.value) {
            console.error("Error updating quarter:", error.value);
            return;
        }
        const updatedQuarter = data.value as Quarter;
        const index = quarters.value.findIndex(q => q.id === id);
        if (index !== -1) quarters.value[index] = updatedQuarter;
        if (current_quarter.value && current_quarter.value.id === id) current_quarter.value = updatedQuarter;
        return updatedQuarter;
    }

        // Delete quarter
    async function deleteQuarter(id: number) {
        if (!quarters.value) {
            console.error("Quarters data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/quarters/${id}`, { method: 'DELETE' });
        if (error.value) {
            console.error('Error deleting quarter:', error.value);
            return;
        }
        quarters.value = quarters.value.filter(p => p.id !== id);
        if (current_quarter.value?.id === id) current_quarter.value = null;
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
