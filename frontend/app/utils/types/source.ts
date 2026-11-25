/**
 * Represents an item from the project's bibliography
 */
export interface Source extends Auditable {
    type: string | null;
    title: string | null;
    authors: string[] | [];
    signature: string | null;
    description: string | null;
    links: string[] | [];
}

/**
 * DTO projection of a {@link Source} object
 */
export interface SourceDTO {
    id: number | null;
    title: string | null;
}
