export const useCitizenshipStore = defineStore("citizenship", () => {
    // State
    const citizenships = ref<CitizenshipDTO[]>([]);
    const current_citizenship = ref<CitizenshipDTO | null>(null);

    // Getters
    const isLoaded = computed(() => citizenships.value.length > 0);

    // Actions
        // Fetch citizenships from the API
    async function fetchCitizenships() {
        if (!isLoaded.value) {
            const { data, error } = await useFetch("/api/citizenships");
            if (error.value) {
                console.error("Error fetching citizenships:", error.value);
                return;
            }
            citizenships.value = data.value as CitizenshipDTO[];
        }
    }

        // Fetch citizenship by ID
    async function fetchCitizenshipById(id: number) {
        if (!current_citizenship.value || current_citizenship.value.id !== id) {
            const cachedCitizenship = citizenships.value.find(citizenship => citizenship.id === id);
            if (cachedCitizenship) {
                current_citizenship.value = cachedCitizenship;
            } else {
                const { data, error } = await useFetch(`/api/citizenships/${id}`);
                if (error.value) {
                    console.error(`Error fetching citizenship by ID: ${ id }`, error.value);
                    return;
                }
                current_citizenship.value = data.value as CitizenshipDTO;
            }
        }
    }

        // Create new citizenship
    async function createCitizenship(payload: Partial<CitizenshipDTO>) {
        const { data, error } = await useFetch('/api/citizenships', {
            method: 'POST',
            body: payload,
        });
        if (error.value) {
            console.error("Error creating citizenship:", error.value);
            return;
        }
        citizenships.value.push(data.value as CitizenshipDTO);
        return data.value;
    }

        // Update existing citizenship
    async function updateCitizenship(payload: Partial<CitizenshipDTO>, id: number) {
        if (citizenships.value.length === 0) {
            console.error("Citizenships data is not loaded");
            return;
        }
        const { data, error } = await useFetch(`/api/citizenships/${id}`, {
            method: 'PUT',
            body: payload,
        });
        if (error.value) {
            console.error(`Error updating citizenship ID ${ id }:`, error.value);
            return;
        }
        const updatedCitizenship = data.value as CitizenshipDTO;
        const index = citizenships.value.findIndex(citizenship => citizenship.id === id);
        if (index !== -1) citizenships.value[index] = updatedCitizenship;
        if (current_citizenship.value && current_citizenship.value.id === id) current_citizenship.value = updatedCitizenship;
        return data.value;
    }

        // Delete citizenship
    async function deleteCitizenship(id: number) {
        if (citizenships.value.length === 0) {
            console.error("Citizenships data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/citizenships/${id}`, { method: 'DELETE' });
        if (error.value) {
            console.error(`Error deleting citizenship ID ${ id }:`, error.value);
            return;
        }
        citizenships.value = citizenships.value.filter(citizenship => citizenship.id !== id);
        if (current_citizenship.value?.id === id) current_citizenship.value = null;
    }

        // Clear current citizenship
    function clearCurrentCitizenship() {
        current_citizenship.value = null;
    }

    return {
        citizenships,
        current_citizenship,
        isLoaded,
        fetchCitizenships,
        fetchCitizenshipById,
        createCitizenship,
        updateCitizenship,
        deleteCitizenship,
        clearCurrentCitizenship
    }
});
