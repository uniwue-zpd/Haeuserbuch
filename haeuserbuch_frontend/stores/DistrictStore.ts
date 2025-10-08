import { ref, computed } from "vue";
import type { District } from "~/utils/types";
import apiClient from "~/service/api";

export const useDistrictStore = defineStore("district", () => {
    // State
    const districts = ref<District[]>([]);
    const current_district = ref<District | null>(null);

    // Getters
    const isLoaded = computed(() => districts.value.length > 0);

    // Actions
        // Fetch districts from the API
    async function fetchDistricts() {
        if (!isLoaded.value) {
            try {
                const response = await apiClient.get<District[]>("/districts");
                districts.value = response.data;
            } catch (error) {
                console.error("Error fetching districts:", error);
            }
        }
    }

        // Fetch district by ID
    async function fetchDistrictById(id: number) {
        if (!current_district.value || current_district.value.id !== id) {
            const cachedDistrict = districts.value.find(district => district.id === id);
            if (cachedDistrict) {
                current_district.value = cachedDistrict;
            } else {
                try {
                    const response = await apiClient.get<District>(`/districts/${id}`);
                    current_district.value = response.data;
                } catch (error) {
                    console.error("Error fetching district by ID:", error);
                }
            }
        }
    }

        // Create new district
    async function createDistrict(payload: Partial<District>) {
        try {
            const response = await apiClient.post<District>('/districts', payload);
            districts.value.push(response.data);
            return response.data;
        } catch (error) {
            console.error("Error creating district:", error);
            throw error;
        }
    }

        // Update existing district
    async function updateDistrict(payload: Partial<District>, id: number) {
        try {
            if (!districts.value.length) {
                console.error("Districts data is not loaded");
                return;
            }
            const response = await apiClient.put<District>(`/districts/${id}`, payload);
            const index = districts.value.findIndex(district => district.id === id);
            if (index !== -1) {
                districts.value[index] = response.data;
            }
            if (current_district.value && current_district.value.id === id) {
                current_district.value = response.data;
            }
            return response.data;
        } catch (error) {
            console.error("Error updating district:", error);
            throw error;
        }
    }

        // Delete district
    async function deleteDistrict(id: number) {
        try {
            await apiClient.delete(`/districts/${id}`);
            districts.value = districts.value.filter(district => district.id !== id);
            if (current_district.value && current_district.value.id === id) {
                current_district.value = null;
            }
        } catch (error) {
            console.error("Error deleting district:", error);
            throw error;
        }
    }

        // Clear current district
    function clearCurrentDistrict() {
        current_district.value = null;
    }

    return {
        districts,
        current_district,
        isLoaded,
        fetchDistricts,
        fetchDistrictById,
        createDistrict,
        updateDistrict,
        deleteDistrict,
        clearCurrentDistrict
    };
});
