export const useWeapons = () => {

    /**
     * Clears cached `useAsyncData` entries containing weapon data, so pages refetch after create/update/delete.
     * Covers "weapons" and "weapon-{id}-…".
     */
    const clearWeaponData = () =>
        clearNuxtData((key) => key.startsWith("weapon"));

    /**
     * `GET` Fetches all weapons.
     * Use it with the `useAsyncData` key "weapons", which is cleared after create/update/delete.
     * @returns Promise resolving to an array of {@link Weapon} entities
     */
    const getWeapons = async (): Promise<Weapon[]> => {
        return await $fetch<Weapon[]>("/api/weapons");
    };

    /**
     * `GET` Fetches a single weapon by its ID.
     * @param id Unique identifier of the weapon
     * @returns Promise resolving to the {@link Weapon} entity
     */
    const getWeapon = async (id: number): Promise<Weapon> => {
        return await $fetch<Weapon>(`/api/weapons/${id}`);
    };

    /**
     * `POST` Creates a new weapon.
     * @param payload Partial weapon data to create
     * @returns Promise resolving to the created {@link Weapon} entity
     */
    const createWeapon = async (payload: Partial<Weapon>): Promise<Weapon> => {
        const data = await $fetch<Weapon>("/api/weapons", { method: "POST", body: payload });
        clearWeaponData();
        return data;
    };

    /**
     * `PUT` Updates an existing weapon.
     * @param id ID of the weapon to update
     * @param payload Partial weapon data to update
     * @returns Promise resolving to the updated {@link Weapon} entity
     */
    const updateWeapon = async (id: number, payload: Partial<Weapon>): Promise<Weapon> => {
        const data = await $fetch<Weapon>(`/api/weapons/${id}`, { method: "PUT", body: payload });
        clearWeaponData();
        return data;
    };

    /**
     * `DELETE` Deletes a weapon by its ID.
     * @param id ID of the weapon to delete
     */
    const deleteWeapon = async (id: number): Promise<void> => {
        await $fetch(`/api/weapons/${id}`, { method: "DELETE" });
        clearWeaponData();
    };

    /**
     * `GET` Searches weapons by name.
     * @param query Search term
     * @returns Promise resolving to an array of {@link WeaponDTO} matching the query
     */
    const searchWeapons = async (query: string): Promise<WeaponDTO[]> => {
        return await $fetch<WeaponDTO[]>("/api/weapons/search", { query: { query } });
    };

    return {
        getWeapons,
        getWeapon,
        createWeapon,
        updateWeapon,
        deleteWeapon,
        searchWeapons
    };
};
