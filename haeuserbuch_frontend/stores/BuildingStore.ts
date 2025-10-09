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
                const { data } = await useFetch("/api/buildings");
                buildings.value = data.value as FeatureCollection;
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
                    const { data } = await useFetch(`/api/buildings/${id}`);
                    current_building.value = data.value as Feature;
                } catch (error) {
                    console.error("Error fetching building by ID:", error);
                }
            }
        }
    }

        // Create new building
    async function createBuilding(payload: Partial<Feature>) {
        try {
            const { data } = await useFetch('/api/buildings', {
                method: 'POST',
                body: payload,
            });

            buildings.value?.features.push(data.value);
            return data.value;
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
            const { data } = await useFetch(`/api/buildings/${id}`, {
                method: 'PUT',
                body: payload
            });
            const index = buildings.value.features.findIndex(feature => feature.id === id);
            if (index !== -1) {
                buildings.value.features[index] = data.value;
            }
            if (current_building.value?.id === id) {
                current_building.value = data.value;
            }
            return data.value;
        } catch (error) {
            console.error("Error updating building:", error);
            return;
        }
    }

        // Delete building
    async function deleteBuilding(id: number) {
        if (!buildings.value) {
            console.error("Buildings data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/buildings/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (error.value) {
            console.error('Error deleting building:', error.value);
            throw error.value;
        }
        buildings.value.features = buildings.value.features.filter(p => p.id !== id);
        if (current_building.value?.id === id) {
            current_building.value = null;
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
