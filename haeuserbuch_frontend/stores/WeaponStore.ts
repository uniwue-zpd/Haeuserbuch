import type { Weapon } from "~/utils/types";

export const useWeaponStore = defineStore('weapon', () => {
    // State
    const weapons = ref<Weapon[]>([]);
    const currentWeapon = ref<Weapon | null>(null);

    // Getters
    const isLoaded = computed(() => weapons.value.length > 0);

    // Actions

    /**
     * GET all weapons from the API and store them in the `weapons` array.
     * Only fetches if the data is not already loaded (checked via `isLoaded` getter).
     */
    async function fetchWeapons() {
        if (!isLoaded.value) {
            const { data, error } = await useFetch("/api/weapons");
            if (error.value) {
                console.error("Error fetching weapons:", error.value);
                return;
            }
            weapons.value = data.value as Weapon[];
        }
    }

    /**
     * GET weapon by ID. First checks if the weapon is already cached in the `weapons` array.
     * @param id ID of the weapon to fetch
     */
    async function fetchWeaponById(id: number) {
        if (!currentWeapon.value || currentWeapon.value.id !== id) {
            const cachedItem = weapons.value.find(weapon => weapon.id === id);
            if (cachedItem) {
                currentWeapon.value = cachedItem;
            } else {
                try {
                    currentWeapon.value = await $fetch<Weapon>(`/api/weapons/${id}`);
                } catch (err) {
                    console.error(`Error fetching weapon by ID: ${ id }`, err);
                    return;
                }
            }
        }
    }

    /**
     * POST Create a new weapon using the given payload.
     * On success, adds the new weapon to the `weapons` array.
     * @param payload Partial weapon data to create
     */
    async function createWeapon(payload: Partial<Weapon>) {
        try {
            const newWeapon = await $fetch<Weapon>('/api/weapons', {
                method: 'POST',
                body: payload,
            });
            weapons.value.push(newWeapon);
            return newWeapon;
        } catch (err) {
            console.error("Error creating weapon:", err);
            return null;
        }
    }

    /**
     * PUT Update an existing weapon by ID using the given payload.
     * @param id ID of the weapon to update
     * @param payload Partial weapon data to update
     */
    async function updateWeapon(id: number, payload: Partial<Weapon>) {
        if (weapons.value.length === 0) {
            console.warn("Weapon list is empty. Fetching weapons before update.");
            return;
        }
        try {
            const updatedWeapon = await $fetch<Weapon>(`/api/weapons/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = weapons.value.findIndex(weapon => weapon.id === id);
            if (index !== -1) weapons.value[index] = updatedWeapon;
        } catch (err) {
            console.error(`Error updating weapon with ID ${id}:`, err);
            return null;
        }
    }

    /**
     * DELETE Remove a weapon by ID.
     * @param id ID of the weapon to delete
     */
    async function deleteWeapon(id: number) {
        if (weapons.value.length === 0) {
            console.warn("Weapons data is not loaded");
            return;
        }
        try {
            await $fetch(`/api/weapons/${id}`, {
                method: 'DELETE',
            });
            weapons.value = weapons.value.filter(weapon => weapon.id !== id);
            if (currentWeapon.value && currentWeapon.value.id === id) currentWeapon.value = null;
        } catch (err) {
            console.error(`Error deleting weapon with ID ${id}:`, err);
            return;
        }
    }

    /**
     * GET an array of weapons based on a search query.
     * @param query Query to be used for searching weapons.
     * @returns An array of `WeaponDTO` matching the search query.
     */
    async function searchWeapons(query: string): Promise<WeaponDTO[]> {
        try {
            return await $fetch<WeaponDTO[]>('/api/weapons/search', { query: { query: query } });
        } catch (err) {
            console.error('Error searching weapons', err);
            return [];
        }
    }

    return {
        weapons,
        currentWeapon,
        isLoaded,
        fetchWeapons,
        fetchWeaponById,
        createWeapon,
        updateWeapon,
        deleteWeapon,
        searchWeapons
    };
});
