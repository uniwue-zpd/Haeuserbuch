import type {FilterPerson} from "~/utils/types";

export const usePersonStore = defineStore("person", () => {
    // State
    const cache = ref<Record<number, PersonDTO>>({});
    const loading = ref(false);

    // Actions

    /**
     * Fetches persons using pageable parameters
     * @param params and sorting parameters: page, size, sort
     * @return Promise resolving to paged person data
     */
    async function fetchPersons(params?: FilterPerson) {
        loading.value = true;
        try {
            return await $fetch(`/api/persons`, { params });
        } finally {
            loading.value = false;
        }
    }

    /**
     * Fetches single person item
     * @param id ID of the entry
     */
    async function fetchPersonById(id: number): Promise<PersonDTO | null> {
        if (cache.value[id]) return cache.value[id];
        try {
            const data = await $fetch(`/api/persons/${id}`);
            cache.value[id] = data;
            return data;
        } catch (error) {
            console.error(`Error fetching person with ID ${ id }:`, error);
            return null;
        }
    }

    /**
     * Fetches an array of people based on various filter parameters.
     * @param params Params to be used
     * @returns An array of `PersonPreviewDTO` matching the filter parameters.
     */
    async function filterPersons(params: FilterPerson): Promise<PersonPreviewDTO[]> {
        try {
            const data = await $fetch('/api/persons/filter', { query: params });
            return data as PersonPreviewDTO[];
        } catch (err) {
            console.error('Error fetching persons by params:', err);
            return [];
        }
    }

    /**
     * Creates a new entry in person register
     * @param payload data to be inserted
     * @return Promise resolving to created person data
     */
    async function createPerson(payload: Partial<PersonDTO>): Promise<PersonDTO> {
        const data = await $fetch<PersonDTO>('/api/persons', {
            method: 'POST',
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * Updates an entry in person register
     * @param id ID of the entry to be updated
     * @param payload data to be updated
     * @return Promise resolving to updated person data
     */
    async function updatePerson(id: number, payload: Partial<PersonDTO>): Promise<PersonDTO> {
        const data = await $fetch<PersonDTO>(`/api/persons/${id}`, {
            method: 'PUT',
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * Deletes an entry in person register
     * @param id ID of the entry
     */
    async function deletePerson(id: number) {
        try {
            await $fetch(`/api/persons/${id}`, { method: 'DELETE' });
            delete cache.value[id];
        } catch (error) {
            console.error("An error appeared: ", error);
            return;
        }
    }

    /**
     * Fetches an array of people based on a search query.
     * @param query Query to be used for searching people.
     * @returns An array of `PersonPreviewDTO` matching the search query.
     */
    async function searchPeople(query: string): Promise<PersonPreviewDTO[]> {
        try {
            return await $fetch<PersonPreviewDTO[]>(`/api/persons/search`, { params: { query: query } });
        } catch (err) {
            console.error('Error searching people:', err);
            return [];
        }
    }

    return {
        fetchPersons,
        fetchPersonById,
        filterPersons,
        createPerson,
        updatePerson,
        deletePerson,
        searchPeople
    }
});
