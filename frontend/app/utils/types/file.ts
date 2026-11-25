/**
 * Document types of a stored file with their German labels.
 * Keys match the backend {@code DocumentType} enum and are sent/received as-is.
 */
export const DOCUMENT_TYPES = {
    PHOTOGRAPH: "Aufnahme",
    PLAN: "Plan",
    MAP: "Karte",
    SITE_PLAN: "Lageplan",
    FLOOR_PLAN: "Grundriss",
    ELEVATION: "Aufriss",
    SECTION: "Schnitt",
    OTHER: "Sonstiges"
} as const;

export type DocumentType = keyof typeof DOCUMENT_TYPES;

/**
 * Descriptive metadata a user can attach to a file on upload.
 * Corresponds to the backend {@code FileMetadata} record.
 */
export interface FileMetadata {
    // Classification
    documentCategory: string | null;
    documentType: DocumentType | null;

    // Dates
    dateCaptured: string | null;
    dateCapturedPrecision: string | null;
    dateFrom: string | null;
    dateTo: string | null;

    // Provenance
    source: string | null;
    collection: string | null;
    signature: string | null;

    // Authorship
    creator: string | null;
    rightsHolder: string | null;
    license: string | null;
    downloadAllowed: boolean | null;

    // Description
    description: string | null;
    caption: string | null;
    sourceUrl: string | null;
}

/**
 * Data transfer object representing a stored file.
 * Contains file and descriptive metadata exposed by the backend API.
 * The DTO does not include internal storage information such as
 * the filesystem path.
 */
export interface FileDTO extends Auditable, FileMetadata {
    originalName: string | null;
    name: string | null;
    type: string | null;
    size: number | null;
}

/**
 * Lightweight data transfer object representing a stored file
 */
export interface FilePreviewDTO {
    id: number;
    originalName: string;
    type: string;
    downloadAllowed: boolean | null;
}
