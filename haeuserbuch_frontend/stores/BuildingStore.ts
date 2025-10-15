import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { Feature, FeatureCollection } from "~/utils/GeoJsonTypes";
import type {BuildingDTO, FilterBuilding} from "~/utils/types";

export const useBuildingStore = defineStore('building', () => {
    // State
    const buildings = ref<FeatureCollection | null>(null);
    const current_building = ref<Feature | null>(null);

    // Getters
    const isLoaded = computed(() => buildings.value !== null);

    // Actions
        // Fetch buildings
    async function fetchBuildings() {
        if (!isLoaded.value) {
            const { data, error } = await useFetch("/api/buildings");
            if (error.value) {
                console.error("Error fetching buildings:", error.value);
                return;
            }
            buildings.value = data.value as FeatureCollection;
        }
    }

        // Fetch building by ID
    async function fetchBuildingById(id: number) {
        if (!current_building.value || current_building.value.id !== id) {
            const cachedBuilding = buildings.value?.features.find(feature => feature.id === id);
            if (cachedBuilding) {
                current_building.value = cachedBuilding;
            } else {
                const { data, error } = await useFetch(`/api/buildings/${id}`);
                if (error.value) {
                    console.error(`Error fetching building by ID :${ id }`, error.value);
                    return;
                }
                current_building.value = data.value as Feature;
            }
        }
    }

        // Filter buildings by IDs of some properties
    async function filterBuildingsByPropertyId(filter: FilterBuilding, id: number) {
        if (!isLoaded.value) {
            return [];
        }
        const {data, error} = await useFetch(`/api/buildings/filter?${filter}Id=${id}`);
        if (error.value) {
            console.error(`Error fetching buildings by ${filter} ID :${id}`, error.value);
            return [];
        }
        return data.value as BuildingDTO[];
    }

        // Create new building
    async function createBuilding(payload: Partial<Feature>) {
        const { data, error } = await useFetch('/api/buildings', {
            method: 'POST',
            body: payload,
        });
        if (error.value) {
            console.error("Error creating building:", error.value);
            return;
        }
        buildings.value?.features.push(data.value as Feature);
        return data.value;
    }

        // Update building by ID
    async function updateBuilding(payload: Partial<Feature>, id: number) {
        if (buildings.value?.features.length === 0 || !buildings.value) {
            console.error('Buildings data is not loaded');
            return;
        }
        const { data, error } = await useFetch(`/api/buildings/${id}`, {
            method: 'PUT',
            body: payload
        });
        if (error.value) {
            console.error("Error updating building:", error.value);
            return;
        }
        const updatedFeature = data.value as Feature;
        const index = buildings.value.features.findIndex(feature => feature.id === id);
        if (index !== -1) buildings.value.features[index] = updatedFeature;
        if (current_building.value?.id === id) current_building.value = updatedFeature;
        return data.value;
    }

        // Delete building
    async function deleteBuilding(id: number) {
        if (!buildings.value) {
            console.error("Buildings data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/buildings/${id}`, { method: 'DELETE' });
        if (error.value) {
            console.error('Error deleting building:', error.value);
            return;
        }
        buildings.value.features = buildings.value.features.filter(p => p.id !== id);
        if (current_building.value?.id === id) current_building.value = null;
    }

        // Clear current building
    function clearCurrentBuilding() {
        current_building.value = null;
    }

    return {
        buildings,
        current_building,
        fetchBuildings,
        fetchBuildingById,
        filterBuildingsByPropertyId,
        createBuilding,
        updateBuilding,
        deleteBuilding,
        clearCurrentBuilding
    }
})
