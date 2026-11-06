import type { BuildingDTO, FilterBuilding } from "~/utils/types";

export const useBuildings = () => {

    /**
     * `GET` all buildings as a `FeatureCollection`.
     * Use it with the `useAsyncData` key "buildings", which is cleared after create/update/delete.
     */
    const getBuildings = async (): Promise<FeatureCollection> => {
        return await $fetch<FeatureCollection>("/api/buildings", { query: { view: "geojson" } });
    };

    const getBuilding = async (id: number): Promise<Feature> => {
        return await $fetch<Feature>(`/api/buildings/${id}`);
    };

    const createBuilding = async (payload: Partial<Feature>): Promise<Feature> => {
        const data = await $fetch<Feature>("/api/buildings", { method: "POST", body: payload });
        clearNuxtData("buildings");
        return data;
    };

    const updateBuilding = async (id: number, payload: Partial<Feature>): Promise<Feature> => {
        const data = await $fetch<Feature>(`/api/buildings/${id}`, { method: "PUT", body: payload });
        clearNuxtData("buildings");
        return data;
    };

    const deleteBuilding = async (id: number): Promise<void> => {
        await $fetch(`/api/buildings/${id}`, { method: "DELETE" });
        clearNuxtData("buildings");
    };

    const searchBuildings = async (query: string): Promise<BuildingDTO[]> => {
        return await $fetch<BuildingDTO[]>("/api/buildings/search", { query: { query } });
    };

    /**
     * `GET` buildings matching the given filters as a `FeatureCollection`.
     */
    const getFilteredBuildings = async (params: FilterBuilding): Promise<FeatureCollection> => {
        return await $fetch<FeatureCollection>("/api/buildings", { query: { ...params, view: "geojson" } });
    };

    /**
     * `GET` lightweight building previews matching the given filters.
     */
    const filterBuildings = async (params: FilterBuilding): Promise<BuildingDTO[]> => {
        return await $fetch<BuildingDTO[]>("/api/buildings", { query: { ...params, view: "preview" } });
    };

    return {
        getBuildings,
        getBuilding,
        createBuilding,
        updateBuilding,
        deleteBuilding,
        searchBuildings,
        getFilteredBuildings,
        filterBuildings
    };
};
