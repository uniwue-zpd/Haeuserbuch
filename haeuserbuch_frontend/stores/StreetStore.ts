import { ref, computed } from "vue";
import type { Street } from "~/utils/types";

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
                const { data } = await useFetch("/api/streets");
                streets.value = data.value as Street[];
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
                    const { data } = await useFetch<Street>(`/api/streets/${id}`);
                    current_street.value = data.value as Street;
                } catch (error) {
                    console.error("Error fetching street by ID:", error);
                }
            }
        }
    }

        // Create new street
    async function createStreet(payload: Partial<Street>) {
        try {
            const { data } = await useFetch('/api/streets', {
                method: 'POST',
                body: payload,
            });
            streets.value.push(data.value as Street);
            return data.value;
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
            const { data } = await useFetch<Street>(`/api/streets/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = streets.value.findIndex(street => street.id === id);
            if (index !== -1) {
                streets.value[index] = data.value as Street;
            }
            if (current_street.value && current_street.value.id === id) {
                current_street.value = data.value as Street;
            }
            return data.value;
        } catch (error) {
            console.error("Error updating street:", error);
            throw error;
        }
    }

        // Delete street by ID
    async function deleteStreet(id: number) {
        if (!streets.value) {
            console.error("Streets data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/streets/${id}`, {
            method: 'DELETE'
        });
        if (error.value) {
            console.error('Error deleting street:', error.value);
            throw error.value;
        }
        streets.value = streets.value.filter(p => p.id !== id);
        if (current_street.value?.id === id) {
            current_street.value = null;
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
