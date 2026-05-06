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
            const { data, error } = await useFetch("/api/streets");
            if (error.value) {
                console.error("Error fetching streets:", error.value);
                return;
            }
            streets.value = data.value as Street[];
        }
    }

        // Fetch street by ID
    async function fetchStreetById(id: number) {
        if (!current_street.value || current_street.value.id !== id) {
            const cachedStreet = streets.value.find(street => street.id === id);
            if (cachedStreet) {
                current_street.value = cachedStreet;
            } else {
                const { data, error } = await useFetch<Street>(`/api/streets/${id}`);
                if (error.value) {
                    console.error(`Error fetching street by ID: ${ id }`, error.value);
                    return;
                }
                current_street.value = data.value as Street;
            }
        }
    }

        // Create new street
    async function createStreet(payload: Partial<Street>) {
        const { data, error } = await useFetch('/api/streets', {
            method: 'POST',
            body: payload,
        });
        if (error.value) {
            console.error("Error creating street:", error.value);
            return;
        }
        streets.value.push(data.value as Street);
        return data.value;
    }

        // Update existing street
    async function updateStreet(payload: Partial<Street>, id: number) {
        if (streets.value.length === 0) {
            console.error("Streets data is not loaded");
            return;
        }
        const { data, error } = await useFetch<Street>(`/api/streets/${id}`, {
            method: 'PUT',
            body: payload,
        });
        if (error.value) {
            console.error("Error updating street:", error.value);
            return;
        }
        const updatedStreet = data.value as Street;
        const index = streets.value.findIndex(street => street.id === id);
        if (index !== -1) streets.value[index] = updatedStreet;
        if (current_street.value && current_street.value.id === id) current_street.value = updatedStreet;
        return data.value;
    }

        // Delete street by ID
    async function deleteStreet(id: number) {
        if (!streets.value) {
            console.error("Streets data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/streets/${id}`, { method: 'DELETE' });
        if (error.value) {
            console.error('Error deleting street:', error.value);
            throw error.value;
        }
        streets.value = streets.value.filter(p => p.id !== id);
        if (current_street.value?.id === id) current_street.value = null;
    }

        // Clear current street
    function clearCurrentStreet() {
        current_street.value = null;
    }

    /**
     * GET An array of streets based on a search query.
     * @param query Search term.
     * @returns An array of StreetDTO objects matching the search query.
     */
    async function searchStreets(query: string): Promise<StreetDTO[]> {
        try {
            return await $fetch<StreetDTO[]>(`/api/streets/search`, { params: { query: query } });
        } catch (err) {
            console.error('Error searching streets:', err);
            return [];
        }
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
        clearCurrentStreet,
        searchStreets
    }
});
