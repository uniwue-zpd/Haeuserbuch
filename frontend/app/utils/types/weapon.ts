/**
 * Represents a weapon type, e.g. "Schwert" etc.
 */
export interface Weapon extends Auditable {
    name: string | null;
    description: string | null;
}

/**
 * Represents a DTO of a weapon.
 */
export interface WeaponDTO {
    id: number | null;
    name: string | null;
    description: string | null;
}
