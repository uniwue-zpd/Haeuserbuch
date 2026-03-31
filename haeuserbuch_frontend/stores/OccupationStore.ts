import type {Occupation} from "~/utils/types";

export const useOccupationStore = defineStore("occupation", () => {
    // State
    const occupations = ref<Occupation[]>([]);
    const currentOccupation = ref<Occupation | null>(null);

    // Getters
    const isLoaded = computed(() => occupations.value.length > 0);

    // Actions

    /**
     * GET all occupations from the API and store them in the `occupations` array.
     * Only fetches if the data is not already loaded (checked via `isLoaded` getter).
     */
    async function fetchOccupations() {
        if (!isLoaded.value) {
            const { data, error } = await useFetch("/api/occupations");
            if (error.value) {
                console.error("Error fetching occupations:", error.value);
                return;
            }
            occupations.value = data.value as Occupation[];
        }
    }

    /**
     * GET occupation by ID. First checks if the occupation is already cached in the `occupations` array.
     * @param id ID of the occupation to fetch
     */
    async function fetchOccupationById(id: number) {
        if (!currentOccupation.value || currentOccupation.value.id !== id) {
            const cachedItem = occupations.value.find(occupation => occupation.id === id);
            if (cachedItem) {
                currentOccupation.value = cachedItem;
            } else {
                try {
                    currentOccupation.value = await $fetch<Occupation>(`/api/occupations/${id}`);
                } catch (err) {
                    console.error(`Error fetching occupation by ID: ${ id }`, err);
                    return;
                }
            }
        }
    }

    /**
     * POST Create a new occupation using the given payload.
     * On success, adds the new occupation to the `occupations` array.
     * @param payload Partial occupation data to create
     */
    async function createOccupation(payload: Partial<Occupation>) {
        try {
            const newOccupation = await $fetch<Occupation>('/api/occupations', {
                method: 'POST',
                body: payload,
            });
            occupations.value.push(newOccupation);
            return newOccupation;
        } catch (err) {
            console.error("Error creating occupation:", err);
            return;
        }
    }

    /**
     * PUT Update an existing occupation by ID using the given payload.
     * On success, updates the corresponding occupation in the `occupations` array.
     * @param payload Partial occupation data to update
     * @param id ID of the occupation to update
     */
    async function updateOccupation(payload: Partial<Occupation>, id: number) {
        if (occupations.value.length === 0) {
            console.error("Occupations data is not loaded");
            return;
        }
        try {
            const updatedOccupation = await $fetch<Occupation>(`/api/occupations/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = occupations.value.findIndex(occupation => occupation.id === id);
            if (index !== -1) occupations.value[index] = updatedOccupation;
            if (currentOccupation.value && currentOccupation.value.id === id) currentOccupation.value = updatedOccupation;
            return updatedOccupation;
        } catch (err) {
            console.error("Error updating occupation:", err);
            return;
        }
    }

    /**
     * DELETE an occupation by ID.
     * On success, removes the occupation from the `occupations` array.
     * @param id ID of the occupation to delete
     */
    async function deleteOccupation(id: number) {
        if (!occupations.value) {
            console.error("Occupations data is not loaded");
            return;
        }
        try {
            await $fetch(`/api/occupations/${id}`, { method: 'DELETE' });
            occupations.value = occupations.value.filter(occupation => occupation.id !== id);
            if (currentOccupation.value && currentOccupation.value.id === id) currentOccupation.value = null;
        } catch (err) {
            console.error('Error deleting occupation:', err);
            return;
        }
    }

    return {
        occupations,
        currentOccupation,
        isLoaded,
        fetchOccupations,
        fetchOccupationById,
        createOccupation,
        updateOccupation,
        deleteOccupation
    };
});
