import type {FilterCitizenship} from "~/utils/types";

export const useCitizenshipStore = defineStore("citizenship", () => {
    // State
    const cache = ref<Record<number, CitizenshipDTO>>({});
    const loading = ref(false);

    // Actions

    /**
     * Fetches citizenships using pageable parameters
     * @param params {@link FilterCitizenship} parameters for filtering and pagination
     * @return Promise resolving to paged citizenship data
     */
    async function fetchCitizenships(params?: FilterCitizenship) {
        loading.value = true;
        try {
            return await $fetch(`/api/citizenships`, { params });
        } finally {
            loading.value = false;
        }
    }

    /**
     * Fetches single citizenship item
     * @param id ID of the entry
     */
    async function fetchCitizenshipById(id: number): Promise<CitizenshipDTO | null> {
        if (cache.value[id]) return cache.value[id];
        try {
            const data = await $fetch(`/api/citizenships/${id}`);
            cache.value[id] = data;
            return data;
        } catch (error) {
            console.error(`Error fetching citizenship ID ${ id }:`, error);
            return null;
        }
    }

    /**
     * Creates a new entry in citizenship register
     * @param payload data to be inserted
     * @return Promise resolving to created citizenship data
     */
    async function createCitizenship(payload: Partial<CitizenshipDTO>): Promise<CitizenshipDTO> {
        const data = await $fetch<CitizenshipDTO>('/api/citizenships', {
            method: 'POST',
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * Updates an entry in citizenship register
     * @param id ID of the entry to be updated
     * @param payload data to be updated
     * @return Promise resolving to updated citizenship data
     */
    async function updateCitizenship(id: number, payload: Partial<CitizenshipDTO>): Promise<CitizenshipDTO> {
        const data = await $fetch<CitizenshipDTO>(`/api/citizenships/${id}`, {
            method: 'PUT',
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * Deletes an entry in citizenship register
     * @param id ID of the entry
     */
    async function deleteCitizenship(id: number) {
        try {
            await $fetch(`/api/citizenships/${id}`, { method: 'DELETE' });
            delete cache.value[id];
        } catch (error) {
            console.error("An error appeared: ", error);
            return;
        }
    }

    return {
        cache,
        fetchCitizenships,
        fetchCitizenshipById,
        createCitizenship,
        updateCitizenship,
        deleteCitizenship,
        loading
    }
});
