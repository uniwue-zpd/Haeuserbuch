import type { Auditable } from "~/utils/types/base_entity";
import type { SourceDTO } from "~/utils/types/source";
import type { Street, StreetDTO } from "~/utils/types/street";

/**
 * Represents a preview DTO of a building.
 */
export interface BuildingDTO {
    id: number | null;
    districtPropertyNumber: string | null;
}

/**
 * Available filters in {@link useBuildings}
 */
export interface FilterBuilding {
    q?: string;
    'district-id'?: number;
    'quarter-id'?: number;
    'street-id'?: number;
    'source-id'?: number;
    georeferenced?: boolean;
}

/**
 * A source attached to a building name, with the relevant excerpt (page reference included in the text).
 */
export interface BuildingNameSource {
    source: SourceDTO | null;
    excerpt: string | null;
}

/**
 * Represents an object containing data of available names of a building.
 */
export interface BuildingName extends Auditable {
    name: string | null;
    fromDate: string | null;
    toDate: string | null;
    sources: BuildingNameSource[];
}

/**
 * Represents an object with data about a name of the {@link BuildingProperties}
 */
export interface BuildingNameDTO {
    id: number | null;
    name: string | null;
    fromDate: string | null;
    toDate: string | null;
    sources: BuildingNameSource[];
}

/**
 * Represents an address of a building
 */
export interface Address extends Auditable {
    street: Street | null;
    houseNumber: string | null;
    fromDate: string | null;
    toDate: string | null;
}

/**
 * DTO projection of an Address object
 */
export interface AddressDTO {
    id: number | null;
    street: StreetDTO | null;
    houseNumber: string | null;
    fromDate: string | null;
    toDate: string | null;
}
