import type { FileDTO, Pageable } from "~/utils/types";

export const useFiles = () => {

    const getAllFiles = async (): Promise<FileDTO[]> => {
        return await $fetch<FileDTO[]>("/api/files/all");
    };

    const getFiles = async (pageable?: Pageable) => {
        return await $fetch("/api/files", {
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

    const uploadFiles = async (files: File[]): Promise<FileDTO[]> => {
        const formData = new FormData();
        files.forEach(file => { formData.append("files", file); });
        return await $fetch<FileDTO[]>("/api/files", { method: "POST", body: formData });
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

    return {
        getAllFiles,
        getFiles,
        getFileById,
        uploadFiles,
        deleteFileById,
        deleteFiles,
        getFileContentUrl
    };
};
