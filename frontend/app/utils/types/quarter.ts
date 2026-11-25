/**
 * Represents a quarter
 */
export interface Quarter extends Auditable {
    name: string | null;
    description: string | null;
}

/**
 * DTO projection of a {@link Quarter} object
 */
export interface QuarterDTO {
    id: number | null;
    name: string | null;
}
