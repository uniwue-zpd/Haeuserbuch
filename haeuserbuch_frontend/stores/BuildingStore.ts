import { defineStore } from "pinia";
import { ref, computed } from "vue";
import apiClient from "~/service/api";
import type { Feature, FeatureCollection } from "~/utils/GeoJsonTypes";

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
            try {
                const response = await apiClient.get<FeatureCollection>("/buildings");
                buildings.value = response.data;
            } catch (error) {
                console.error("Error fetching buildings:", error);
            }
        }
    }

        // Fetch building by ID
    async function fetchBuildingById(id: number) {
        if (!current_building.value || current_building.value.id !== id) {
            const cachedBuilding = buildings.value?.features.find(feature => feature.id === id);
            if (cachedBuilding) {
                current_building.value = cachedBuilding;
            } else {
                try {
                    const response = await apiClient.get<Feature>(`/buildings/${id}`);
                    current_building.value = response.data;
                } catch (error) {
                    console.error("Error fetching building by ID:", error);
                }
            }
        }
    }

        // Create new building
    async function createBuilding(payload: Partial<Feature>) {
        try {
            const response = await apiClient.post('/buildings', payload);
            buildings.value?.features.push(response.data);
            return response.data;
        } catch (error) {
            console.error("Error creating building:", error);
            throw error;
        }
    }

        // Update building by ID
    async function updateBuilding(payload: Partial<Feature>, id: number) {
        try {
            if (!buildings.value) {
                console.error("Buildings data is not loaded");
                return;
            }
            const response = await apiClient.put(`/buildings/${id}`, payload);
            const index = buildings.value.features.findIndex(feature => feature.id === id);
            if (index !== -1) {
                buildings.value.features[index] = response.data;
            }
            if (current_building.value?.id === id) {
                current_building.value = response.data;
            }
            return response.data;
        } catch (error) {
            console.error("Error updating building:", error);
            return;
        }
    }

        // Delete building
    async function deleteBuilding(id: number) {
        try {
            if (!buildings.value) {
                console.error("Buildings data is not loaded");
                return;
            }
            await apiClient.delete(`/buildings/${id}`);
            buildings.value.features = buildings.value.features.filter(p => p.id !== id);
            if (current_building.value?.id === id) {
                current_building.value = null;
            }
        } catch (error) {
            console.log('Error deleting building:', error);
            throw error;
        }
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
        createBuilding,
        updateBuilding,
        deleteBuilding,
        clearCurrentBuilding
    }
})
