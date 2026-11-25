/**
 * Represents metadata fields that are available in all entities of the project.
 */
export interface Auditable {
    id: number;
    createdDate: string;
    createdBy: string | null;
    lastModifiedDate: string;
    lastModifiedBy: string | null;
    internalNotes: string | null;
    generalNotes: string | null;
}
