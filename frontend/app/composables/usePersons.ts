import type { FilterPerson, Page, PersonDTO, PersonPreviewDTO } from "~/utils/types";

export const usePersons = () => {

    /** Clears cached `useAsyncData` entries containing person data, so pages refetch after create/update/delete. */
    const clearPersonData = () =>
        clearNuxtData((key) => key.startsWith("person") || key.includes("people"));

    /**
     * `GET` a page of persons, optionally filtered and sorted.
     */
    const getPersons = async (params?: FilterPerson): Promise<Page<PersonDTO>> => {
        return await $fetch<Page<PersonDTO>>("/api/persons", { query: params });
    };

    const getPerson = async (id: number): Promise<PersonDTO> => {
        return await $fetch<PersonDTO>(`/api/persons/${id}`);
    };

    /**
     * `GET` lightweight person previews matching the given filters, sorted by full name.
     */
    const filterPersons = async (params: FilterPerson): Promise<PersonPreviewDTO[]> => {
        return await $fetch<PersonPreviewDTO[]>("/api/persons", { query: { ...params, view: "preview" } });
    };

    /**
     * Searches persons by name and alternative spellings. Queries shorter than 3 characters return nothing.
     */
    const searchPersons = async (query: string): Promise<PersonPreviewDTO[]> => {
        if (query.trim().length < 3) return [];
        return await filterPersons({ name: query });
    };

    const createPerson = async (payload: Partial<PersonDTO>): Promise<PersonDTO> => {
        const data = await $fetch<PersonDTO>("/api/persons", { method: "POST", body: payload });
        clearPersonData();
        return data;
    };

    const updatePerson = async (id: number, payload: Partial<PersonDTO>): Promise<PersonDTO> => {
        const data = await $fetch<PersonDTO>(`/api/persons/${id}`, { method: "PUT", body: payload });
        clearPersonData();
        return data;
    };

    const deletePerson = async (id: number): Promise<void> => {
        await $fetch(`/api/persons/${id}`, { method: "DELETE" });
        clearPersonData();
    };

    return {
        getPersons,
        getPerson,
        filterPersons,
        searchPersons,
        createPerson,
        updatePerson,
        deletePerson
    };
};
