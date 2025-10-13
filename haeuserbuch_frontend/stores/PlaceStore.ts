import { defineStore } from "pinia";
import { ref, computed } from "vue";
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
            const { data, error } = await useFetch('/api/places');
            if (error.value) {
                console.error("Error fetching places:", error.value);
                return;
            }
            places.value = data.value as FeatureCollection;
        }
    }

        // Fetch place by ID
    async function fetchPlaceById(id: number) {
        if (!current_place.value || current_place.value.id !== id) {
            const cachedPlace = places.value?.features.find(feature => feature.id === id);
            if (cachedPlace) {
                current_place.value = cachedPlace;
            } else {
                const { data, error } = await useFetch<Feature>(`/api/places/${id}`);
                if (error.value) {
                    console.error(`Error fetching place by ID: ${ id }`, error.value);
                    return;
                }
                current_place.value = data.value as Feature;
            }
        }
    }

        // Create new place
    async function createPlace(payload: Partial<Feature>) {
        const { data, error } = await useFetch('/api/places', {
            method: 'POST',
            body: payload
        });
        if (error.value) {
            console.error("Error creating place:", error.value);
            return;
        }
        places.value?.features.push(data.value as Feature);
        return data.value;
    }

        // Update existing place
    async function updatePlace(payload: Partial<Feature>, id: number) {
        if (places.value?.features.length === 0 || !places.value) {
            console.error("Places data is not loaded");
            return;
        }
        const { data, error } = await useFetch<Feature>(`/api/places/${id}`, {
            method: 'PUT',
            body: payload
        });
        if (error.value) {
            console.error("Error updating place:", error.value);
            return;
        }
        const updatedPlace = data.value as Feature;
        const index = places.value.features.findIndex(feature => feature.id === id);
        if (index !== -1) places.value.features[index] = updatedPlace;
        if (current_place.value?.id === id) current_place.value = updatedPlace;
        return data.value;
    }

        // Delete place by ID
    async function deletePlace(id: number) {
        if (!places.value) {
            console.error("Places data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/places/${id}`, { method: 'DELETE' });
        if (error.value) {
            console.error('Error deleting places:', error.value);
            return
        }
        places.value.features = places.value.features.filter(p => p.id !== id);
        if (current_place.value?.id === id) current_place.value = null;
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
