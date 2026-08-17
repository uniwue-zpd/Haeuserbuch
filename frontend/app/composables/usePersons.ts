export const usePersons = () => {

    const getAllPersons = async (): Promise<PersonDTO[]> => {
        return await $fetch(`/api/persons/all`);
    }

    const getPagedPersons = async (pageable?: FilterPerson): Promise<PersonDTO[]> => {
        return await $fetch(`/api/persons`, { query: pageable });
    }

    const filterPersons = async (params: FilterPerson): Promise<PersonPreviewDTO[]> => {
        return await $fetch(`/api/persons/filter`, { query: params });
    }

    const getPersonById = async (id: number): Promise<PersonDTO> => {
        return await $fetch(`/api/persons/${id}`);
    }

    const createPerson = async (payload: Partial<PersonDTO>): Promise<PersonDTO> => {
        return await $fetch(`/api/persons`, {
            method: 'POST',
            body: payload
        });
    }

    const updatePerson = async (id: number, payload: Partial<PersonDTO>): Promise<PersonDTO> => {
        return await $fetch(`/api/persons/${ id }`, {
            method: 'PUT',
            body: payload
        });
    }

    const deletePerson = async (id: number): Promise<void> => {
        await $fetch(`/api/persons/${ id }`, {
            method: 'DELETE'
        });
    }

    const searchPersonsByName = async (query: string): Promise<PersonPreviewDTO[]> => {
        return await $fetch(`/api/persons/search`, { query: { query } });
    }

    return {
        getAllPersons,
        getPagedPersons,
        filterPersons,
        getPersonById,
        createPerson,
        updatePerson,
        deletePerson,
        searchPersonsByName
    }
}
