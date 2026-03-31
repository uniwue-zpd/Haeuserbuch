import type { Religion } from "~/utils/types";

export const useReligionStore = defineStore("religion", () => {
    // State
    const religions = ref<Religion[]>([]);
    const currentReligion = ref<Religion | null>(null);

    // Getters
    const isLoaded = computed(() => religions.value.length > 0);

    // Actions

    /**
     * GET all religions from the API and store them in the `religions` array.
     * Only fetches if the data is not already loaded (checked via `isLoaded` getter).
     */
    async function fetchReligions() {
        if (!isLoaded.value) {
            const { data, error } = await useFetch("/api/religions");
            if (error.value) {
                console.error("Error fetching religions:", error.value);
                return;
            }
            religions.value = data.value as Religion[];
        }
    }

    /**
     * GET religion by ID. First checks if the religion is already cached in the `religions` array.
     * On success, sets the `currentReligion` to the fetched religion.
     * @param id ID of the religion to fetch
     */
    async function fetchReligionById(id: number) {
        if (!currentReligion.value || currentReligion.value.id !== id) {
            const cachedItem = religions.value.find(religion => religion.id === id);
            if (cachedItem) {
                currentReligion.value = cachedItem;
            } else {
                try {
                    currentReligion.value = await $fetch<Religion>(`/api/religions/${id}`);
                } catch (err) {
                    console.error(`Error fetching religion by ID: ${ id }`, err);
                    return;
                }
            }
        }
    }

    /**
     * POST Create a new religion using the given payload.
     * On success, adds the new religion to the `religions` array.
     * @param payload Partial religion data to create
     */
    async function createReligion(payload: Partial<Religion>) {
        try {
            const newReligion = await $fetch<Religion>('/api/religions', {
                method: 'POST',
                body: payload,
            });
            religions.value.push(newReligion);
            return newReligion;
        } catch (err) {
            console.error("Error creating religion:", err);
            return null;
        }
    }

    /**
     * PUT Update an existing religion by ID using the given payload.
     * On success, updates the corresponding religion in the `religions` array and `currentReligion` if it was the updated one.
     * @param id ID of the religion to update
     * @param payload Partial religion data to update
     */
    async function updateReligion(id: number, payload: Partial<Religion>) {
        try {
            const updatedReligion = await $fetch<Religion>(`/api/religions/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = religions.value.findIndex(religion => religion.id === id);
            if (index !== -1) religions.value[index] = updatedReligion;
            if (currentReligion.value && currentReligion.value.id === id) currentReligion.value = updatedReligion;
            return updatedReligion;
        } catch (err) {
            console.error(`Error updating religion with ID ${id}:`, err);
            return null;
        }
    }

    /**
     * DELETE an existing religion by ID.
     * On success, removes the religion from the `religions` array and clears `currentReligion` if it was the deleted one.
     * @param id ID of the religion to delete
     */
    async function deleteReligion(id: number) {
        if (religions.value.length === 0) {
            console.error("Religions data is not loaded");
            return;
        }
        try {
            await $fetch(`/api/religions/${id}`, { method: 'DELETE' });
            religions.value = religions.value.filter(religion => religion.id !== id);
            if (currentReligion.value && currentReligion.value.id === id) currentReligion.value = null;
        } catch (err) {
            console.error(`Error deleting religion with ID ${id}:`, err);
        }
    }

    return {
        religions,
        currentReligion,
        isLoaded,
        fetchReligions,
        fetchReligionById,
        createReligion,
        updateReligion,
        deleteReligion
    };
});
