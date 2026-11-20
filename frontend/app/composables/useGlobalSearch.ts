import type { GlobalSearchParams, GlobalSearchResponse } from "~/utils/globalSearch";

export const useGlobalSearch = () => {

    /**
     * `GET` search results across all entity types, with optional facets.
     * Pass an `AbortSignal` to cancel superseded requests (e.g. while typing).
     */
    const search = async (params: GlobalSearchParams, signal?: AbortSignal): Promise<GlobalSearchResponse> => {
        return await $fetch<GlobalSearchResponse>("/api/search", {
            signal,
            query: {
                query: params.query,
                fields: params.fields?.join(","),
                types: params.types?.join(","),
                fuzzy: params.fuzzy ?? !(params.exactFullText ?? false),
                page: params.page ?? 0,
                size: params.size ?? 20,
                includeFacets: params.includeFacets ?? true,
            },
        });
    };

    return {
        search
    };
};
