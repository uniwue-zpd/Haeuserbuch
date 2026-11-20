import type {
    CitizenshipFullTextResult,
    CitizenshipPreviewDTO,
    FilterCitizenship,
    Page,
    SearchCitizenshipFullText
} from "~/utils/types";

export const useCitizenships = () => {

    /**
     * Clears cached `useAsyncData` entries containing citizenship data, so pages refetch after create/update/delete.
     * Includes the naturalization/guarantor entries cached on person pages.
     */
    const clearCitizenshipData = () =>
        clearNuxtData(
            (key) =>
            key.startsWith("citizenship")
            || key.endsWith("-naturalization")
            || key.endsWith("-guarantor")
        );

    /**
     * `GET` a page of citizenship entries, optionally filtered and sorted.
     */
    const getCitizenships = async (params?: FilterCitizenship): Promise<Page<CitizenshipDTO>> => {
        return await $fetch<Page<CitizenshipDTO>>("/api/citizenships", { query: params });
    };

    const getCitizenship = async (id: number): Promise<CitizenshipDTO> => {
        return await $fetch<CitizenshipDTO>(`/api/citizenships/${id}`);
    };

    /**
     * `GET` lightweight citizenship previews matching the given filters.
     */
    const filterCitizenships = async (params: FilterCitizenship): Promise<CitizenshipPreviewDTO[]> => {
        return await $fetch<CitizenshipPreviewDTO[]>("/api/citizenships/filter", { query: params });
    };

    /**
     * Full-text search over citizenship entries, paged.
     */
    const searchCitizenshipsFullText = async (params: SearchCitizenshipFullText): Promise<Page<CitizenshipFullTextResult>> => {
        return await $fetch<Page<CitizenshipFullTextResult>>("/api/citizenships/fulltextsearch", { query: params });
    };

    const createCitizenship = async (payload: Partial<CitizenshipDTO>): Promise<CitizenshipDTO> => {
        const data = await $fetch<CitizenshipDTO>("/api/citizenships", { method: "POST", body: payload });
        clearCitizenshipData();
        return data;
    };

    const updateCitizenship = async (id: number, payload: Partial<CitizenshipDTO>): Promise<CitizenshipDTO> => {
        const data = await $fetch<CitizenshipDTO>(`/api/citizenships/${id}`, { method: "PUT", body: payload });
        clearCitizenshipData();
        return data;
    };

    const deleteCitizenship = async (id: number): Promise<void> => {
        await $fetch(`/api/citizenships/${id}`, { method: "DELETE" });
        clearCitizenshipData();
    };

    return {
        getCitizenships,
        getCitizenship,
        filterCitizenships,
        searchCitizenshipsFullText,
        createCitizenship,
        updateCitizenship,
        deleteCitizenship
    };
};
