/**
 * Represents an entity containing data about some job.
 */
export interface Job extends Auditable {
    name: string | null;
    altNames: string[] | [];
    description: string | null;
}

/**
 * DTO representation of some job entity.
 */
export interface JobDTO {
    id: number | null;
    name: string | null;
    description: string | null;
}
