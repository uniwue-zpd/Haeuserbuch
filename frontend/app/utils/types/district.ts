/**
 * An object describing a district.
 */
export interface District extends Auditable {
    name: string | null;
    description: string | null;
}

/**
 * DTO projection of a {@link District} object
 */
export interface DistrictDTO {
    id: number | null;
    name: string | null;
}
