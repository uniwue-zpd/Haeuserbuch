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
                const { data } = await useFetch("/api/districts");
                districts.value = data.value as District[];
                districts.value = data.value;
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
                    const { data } = await useFetch(`/api/districts/${id}`);
                    current_district.value = data.value as District;
                } catch (error) {
                    console.error("Error fetching district by ID:", error);
                }
            }
        }
    }

        // Create new district
    async function createDistrict(payload: Partial<District>) {
        try {
            const { data } = await useFetch('/api/districts', {
                method: 'POST',
                body: payload,
            });
            districts.value.push(data.value as District);
            return data.value;
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
            const { data } = await useFetch(`/api/districts/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = districts.value.findIndex(district => district.id === id);
            if (index !== -1) {
                districts.value[index] = data.value as District;
            }
            if (current_district.value && current_district.value.id === id) {
                current_district.value = data.value as District;
            }
            return data.value;
        } catch (error) {
            console.error("Error updating district:", error);
            throw error;
        }
    }

        // Delete district
    async function deleteDistrict(id: number) {
        if (!districts.value) {
            console.error("Districts data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/districts/${id}`, {
            method: 'DELETE'
        });
        if (error.value) {
            console.error('Error deleting district:', error.value);
            throw error.value;
        }
        districts.value = districts.value.filter(p => p.id !== id);
        if (current_district.value?.id === id) {
            current_district.value = null;
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
