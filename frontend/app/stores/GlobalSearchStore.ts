import type { GlobalSearchParams, GlobalSearchResponse } from "~/utils/globalSearch";

export const useGlobalSearchStore = defineStore("global-search", () => {
  async function search(params: GlobalSearchParams, signal?: AbortSignal): Promise<GlobalSearchResponse> {
    return await $fetch<GlobalSearchResponse>("/api/search", {
      signal,
      query: {
        query: params.query,
        fields: params.fields?.join(","),
        types: params.types?.join(","),
        exactFullText: params.exactFullText ?? false,
        page: params.page ?? 0,
        size: params.size ?? 20,
        includeFacets: params.includeFacets ?? true,
      },
    });
  }

  return { search };
});
