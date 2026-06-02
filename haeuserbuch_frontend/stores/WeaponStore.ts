export const useWeaponStore = defineStore("weapon", () => {
    // State
    const cache = ref<Record<number, Weapon>>({});
    const loading = ref(false);

    // Actions

    /**
     * `GET` Fetch all weapons from backend.
     * @returns Promise resolving to an array of Weapon entities
     */
    async function fetchWeapons(): Promise<Weapon[]> {
        loading.value = true;
        try {
            return await $fetch<Weapon[]>("/api/weapons");
        } finally {
            loading.value = false;
        }
    }

    /**
     * `GET` Fetch a single weapon by ID.
     * @param id Unique identifier
     * @returns Weapon or null if request fails
     */
    async function fetchWeaponById(id: number): Promise<Weapon | null> {
        if (cache.value[id]) return cache.value[id];
        try {
            const data = await $fetch<Weapon>(`/api/weapons/${id}`);
            cache.value[id] = data;
            return data;
        } catch (error) {
            console.error(`Error fetching weapon with ID ${id}:`, error);
            return null;
        }
    }

    /**
     * `POST` Create a new weapon.
     * @param payload Partial weapon data
     * @returns Created Weapon
     */
    async function createWeapon(payload: Partial<Weapon>): Promise<Weapon> {
        const data = await $fetch<Weapon>("/api/weapons", {
            method: "POST",
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * `PUT` Update an existing weapon.
     * @param id Weapon ID
     * @param payload Partial update data
     * @returns Updated Weapon
     */
    async function updateWeapon(id: number, payload: Partial<Weapon>): Promise<Weapon> {
        const data = await $fetch<Weapon>(`/api/weapons/${id}`, {
            method: "PUT",
            body: payload
        });
        cache.value[id] = data;
        return data;
    }

    /**
     * `DELETE` Remove a weapon by ID.
     * @param id Weapon ID
     */
    async function deleteWeapon(id: number): Promise<void> {
        try {
            await $fetch(`/api/weapons/${id}`, { method: "DELETE" });
            delete cache.value[id];
        } catch (error) {
            console.error(`Error deleting weapon with ID ${id}:`, error);
        }
    }

    /**
     * `GET` Search weapons using query string.
     * @param query Search term
     * @returns Array of WeaponDTO
     */
    async function searchWeapons(query: string): Promise<WeaponDTO[]> {
        try {
            return await $fetch<WeaponDTO[]>("/api/weapons/search", {
                params: { query }
            });
        } catch (error) {
            console.error("Error searching weapons:", error);
            return [];
        }
    }

    return {
        cache,
        loading,
        fetchWeapons,
        fetchWeaponById,
        createWeapon,
        updateWeapon,
        deleteWeapon,
        searchWeapons
    };
});
