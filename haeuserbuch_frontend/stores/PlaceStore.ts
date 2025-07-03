import { defineStore } from "pinia";
import { ref, computed } from "vue";
import apiClient from "~/service/api";
import type { Feature, FeatureCollection } from "~/utils/GeoJsonTypes";

export const usePlaceStore = defineStore("place", () => {
    // State
    const places = ref<FeatureCollection | null>(null);
    const current_place = ref<Feature | null>(null);

    // Getters
    const isLoaded = computed(() => places.value !== null);

    // Actions
        // Fetch places from the API
    async function fetchPlaces() {
        if (!isLoaded.value) {
            try {
                const response = await apiClient.get<FeatureCollection>("/places");
                places.value = response.data;
            } catch (error) {
                console.error("Error fetching places:", error);
            }
        }
    }

        // Fetch place by ID
    async function fetchPlaceById(id: number) {
        if (!current_place.value || current_place.value.id !== id) {
            const cachedPlace = places.value?.features.find(feature => feature.id === id);
            if (cachedPlace) {
                current_place.value = cachedPlace;
            } else {
                try {
                    const response = await apiClient.get<Feature>(`/places/${id}`);
                    current_place.value = response.data;
                } catch (error) {
                    console.error("Error fetching place by ID:", error);
                }
            }
        }
    }

        // Create new place
    async function createPlace(payload: Partial<Feature>) {
        try {
            const response = await apiClient.post('/places', payload);
            places.value?.features.push(response.data);
            return response.data;
        } catch (error) {
            console.error("Error creating place:", error);
            throw error;
        }
    }

        // Update existing place
    async function updatePlace(payload: Partial<Feature>, id: number) {
        try {
            if (!places.value) {
                console.error("Places data is not loaded");
                return;
            }
            const response = await apiClient.put(`/places/${id}`, payload);
            const index = places.value.features.findIndex(feature => feature.id === id);
            if (index !== -1) {
                places.value.features[index] = response.data;
            }
            if (current_place.value?.id === id) {
                current_place.value = response.data;
            }
            return response.data;
        } catch (error) {
            console.error("Error updating place:", error);
            return;
        }
    }

        // Delete place by ID
    async function deletePlace(id: number) {
        try {
            if (!places.value) {
                console.error("Places data is not loaded");
                return;
            }
            await apiClient.delete(`/places/${id}`);
            places.value.features = places.value.features.filter(p => p.id !== id);
            if (current_place.value?.id === id) {
                current_place.value = null;
            }
        } catch (error) {
            console.log('Error deleting building:', error);
            throw error;
        }
    }

        // Clear current place
    function clearCurrentPlace() {
        current_place.value = null;
    }

    return {
        places,
        current_place,
        fetchPlaces,
        fetchPlaceById,
        createPlace,
        updatePlace,
        deletePlace,
        clearCurrentPlace
    }
});
