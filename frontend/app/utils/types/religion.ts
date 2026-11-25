/**
 * Represents an entity containing data about some religion.
 */
export interface Religion extends Auditable {
    name: string | null;
    description: string | null;
}

/**
 * DTO representation of some religion entity.
 */
export interface ReligionDTO {
    id: number | null;
    name: string | null;
    description: string | null;
}
