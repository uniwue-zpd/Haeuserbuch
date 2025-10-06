import { ref, computed } from "vue";
import type { Street } from "~/utils/types";
import apiClient from "~/service/api";

export const useStreetStore = defineStore("street", () => {
    // State
    const streets = ref<Street[]>([]);
    const current_street = ref<Street | null>(null);

    // Getters
    const isLoaded = computed(() => streets.value.length > 0);

    // Actions
        // Fetch streets from the API
    async function fetchStreets() {
        if (!isLoaded.value) {
            try {
                const response = await apiClient.get<Street[]>("/streets");
                streets.value = response.data;
            } catch (error) {
                console.error("Error fetching streets:", error);
            }
        }
    }

        // Fetch street by ID
    async function fetchStreetById(id: number) {
        if (!current_street.value || current_street.value.id !== id) {
            const cachedStreet = streets.value.find(street => street.id === id);
            if (cachedStreet) {
                current_street.value = cachedStreet;
            } else {
                try {
                    const response = await apiClient.get<Street>(`/streets/${id}`);
                    current_street.value = response.data;
                } catch (error) {
                    console.error("Error fetching street by ID:", error);
                }
            }
        }
    }

        // Create new street
    async function createStreet(payload: Partial<Street>) {
        try {
            const response = await apiClient.post<Street>('/streets', payload);
            streets.value.push(response.data);
            return response.data;
        } catch (error) {
            console.error("Error creating street:", error);
            throw error;
        }
    }

        // Update existing street
    async function updateStreet(payload: Partial<Street>, id: number) {
        try {
            if (!streets.value.length) {
                console.error("Streets data is not loaded");
                return;
            }
            const response = await apiClient.put<Street>(`/streets/${id}`, payload);
            const index = streets.value.findIndex(street => street.id === id);
            if (index !== -1) {
                streets.value[index] = response.data;
            }
            if (current_street.value && current_street.value.id === id) {
                current_street.value = response.data;
            }
            return response.data;
        } catch (error) {
            console.error("Error updating street:", error);
            throw error;
        }
    }

        // Delete street by ID
    async function deleteStreet(id: number) {
        try {
            await apiClient.delete(`/streets/${id}`);
            streets.value = streets.value.filter(street => street.id !== id);
            if (current_street.value && current_street.value.id === id) {
                current_street.value = null;
            }
        } catch (error) {
            console.error("Error deleting street:", error);
            throw error;
        }
    }

        // Clear current street
    function clearCurrentStreet() {
        current_street.value = null;
    }

    return {
        streets,
        current_street,
        isLoaded,
        fetchStreets,
        fetchStreetById,
        createStreet,
        updateStreet,
        deleteStreet,
        clearCurrentStreet
    }
});
