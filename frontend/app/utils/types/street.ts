import type { Auditable } from "~/utils/types/base_entity";

/**
 Represents a street
 */
export interface Street extends Auditable {
    name: string | null;
    altNames: string[] | [];
    description: string | null;
}

/**
 * DTO projection of a {@link Street} object
 */
export interface StreetDTO {
    id: number | null;
    name: string | null;
}
