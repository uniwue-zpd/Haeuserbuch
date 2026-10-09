import type { FileDTO, FileMetadata, Page, Pageable } from "~/utils/types";

export const useFiles = () => {

    const getAllFiles = async (): Promise<FileDTO[]> => {
        return await $fetch<FileDTO[]>("/api/files/all");
    };

    const getFiles = async (pageable?: Pageable): Promise<Page<FileDTO>> => {
        return await $fetch<Page<FileDTO>>("/api/files", {
            query: {
                page: pageable?.page ?? 0,
                size: pageable?.size ?? 10,
                sort: pageable?.sort
            }
        });
    };

    const getFileById = async (id: number): Promise<FileDTO> => {
        return await $fetch<FileDTO>(`/api/files/${id}`);
    };

    const jsonPart = (value: unknown): Blob => {
        return new Blob([JSON.stringify(value)], { type: "application/json" });
    };

    const uploadFile = async (file: File, metadata?: Partial<FileMetadata>): Promise<FileDTO> => {
        const formData = new FormData();
        formData.append("file", file);
        if (metadata) formData.append("metadata", jsonPart(metadata));
        return await $fetch<FileDTO>("/api/files", { method: "POST", body: formData });
    };

    /**
     * Uploads several files at once.
     * If provided, metadata[i] is applied to files[i]; both arrays must have the same length.
     */
    const uploadFiles = async (
        files: File[],
        metadata?: (Partial<FileMetadata> | null)[]
    ): Promise<FileDTO[]> => {
        const formData = new FormData();
        files.forEach(file => { formData.append("files", file); });
        if (metadata) formData.append("metadata", jsonPart(metadata));
        return await $fetch<FileDTO[]>("/api/files/batch", { method: "POST", body: formData });
    };

    const deleteFileById = async (id: number): Promise<void> => {
        await $fetch(`/api/files/${id}`, { method: "DELETE" });
    };

    const deleteFiles = async (ids: number[]): Promise<{
        success: number[];
        fail: number[];
        notFound: number[];
    }> => {
        return await $fetch("/api/files", { method: "DELETE", body: ids });
    };

    const getFileContentUrl = (id: number): string => {
        return `/api/files/${id}/content`;
    };

    /**
     * URL that serves the file as an attachment. The backend answers 403 if downloads are not allowed.
     */
    const getFileDownloadUrl = (id: number): string => {
        return `/api/files/${id}/content?download=true`;
    };

    const searchFiles = async (query: string): Promise<FileDTO[]> => {
        return await $fetch<FileDTO[]>("/api/files/search", { query: { query } });
    };

    return {
        getAllFiles,
        getFiles,
        getFileById,
        uploadFile,
        uploadFiles,
        deleteFileById,
        deleteFiles,
        getFileContentUrl,
        getFileDownloadUrl,
        searchFiles
    };
};
