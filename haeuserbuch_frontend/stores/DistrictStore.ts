import { ref, computed } from "vue";
import type { District } from "~/utils/types";

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
            const { data, error } = await useFetch("/api/districts");
            if (error.value) {
                console.error("Error fetching districts:", error.value);
                return;
            }
            districts.value = data.value as District[];
        }
    }

        // Fetch district by ID
    async function fetchDistrictById(id: number) {
        if (!current_district.value || current_district.value.id !== id) {
            const cachedDistrict = districts.value.find(district => district.id === id);
            if (cachedDistrict) {
                current_district.value = cachedDistrict;
            } else {
                const { data, error } = await useFetch(`/api/districts/${id}`);
                if (error.value) {
                    console.error(`Error fetching district by ID: ${ id }`, error.value);
                    return;
                }
                current_district.value = data.value as District;
            }
        }
    }

        // Create new district
    async function createDistrict(payload: Partial<District>) {
        const { data, error } = await useFetch('/api/districts', {
            method: 'POST',
            body: payload,
        });
        if (error.value) {
            console.error("Error creating district:", error.value);
            return;
        }
        districts.value.push(data.value as District);
        return data.value;
    }

        // Update existing district
    async function updateDistrict(payload: Partial<District>, id: number) {
        if (districts.value.length === 0) {
            console.error("Districts data is not loaded");
            return;
        }
        const { data, error } = await useFetch(`/api/districts/${id}`, {
            method: 'PUT',
            body: payload,
        });
        if (error.value) {
            console.error("Error updating district:", error.value);
            return;
        }
        const updatedDistrict = data.value as District;
        const index = districts.value.findIndex(district => district.id === id);
        if (index !== -1) districts.value[index] = updatedDistrict;
        if (current_district.value && current_district.value.id === id) current_district.value = updatedDistrict;
        return updatedDistrict;
    }

        // Delete district
    async function deleteDistrict(id: number) {
        if (!districts.value) {
            console.error("Districts data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/districts/${id}`, { method: 'DELETE' });
        if (error.value) console.error('Error deleting district:', error.value);
        districts.value = districts.value.filter(p => p.id !== id);
        if (current_district.value?.id === id) current_district.value = null;
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
