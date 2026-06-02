export const useJobStore = defineStore("job", () => {
    // State
    const cache = ref<Record<number, Job>>({});
    const loading = ref(false);

    // Actions

    /**
     * GET all jobs
     */
    async function fetchJobs(): Promise<Job[]> {
        loading.value = true;
        try {
            return await $fetch<Job[]>("/api/jobs");
        } finally {
            loading.value = false;
        }
    }

    /**
     * GET job by ID (cached)
     */
    async function fetchJobById(id: number): Promise<Job | null> {
        if (cache.value[id]) return cache.value[id];
        try {
            const data = await $fetch<Job>(`/api/jobs/${id}`);
            cache.value[id] = data;
            return data;
        } catch (err) {
            console.error(`Error fetching job ${id}:`, err);
            return null;
        }
    }

    /**
     * POST create job
     */
    async function createJob(payload: Partial<Job>): Promise<Job> {
        const data = await $fetch<Job>("/api/jobs", {
            method: "POST",
            body: payload
        });
        cache.value[data.id] = data;
        return data;
    }

    /**
     * PUT update job
     */
    async function updateJob(id: number, payload: Partial<Job>): Promise<Job> {
        const data = await $fetch<Job>(`/api/jobs/${id}`, {
            method: "PUT",
            body: payload
        });
        cache.value[id] = data;
        return data;
    }

    /**
     * DELETE job
     */
    async function deleteJob(id: number): Promise<void> {
        try {
            await $fetch(`/api/jobs/${id}`, { method: "DELETE" });
            delete cache.value[id];
        } catch (err) {
            console.error(`Error deleting job ${id}:`, err);
        }
    }

    /**
     * SEARCH jobs (DTO)
     */
    async function searchJobs(query: string): Promise<JobDTO[]> {
        try {
            return await $fetch<JobDTO[]>("/api/jobs/search", {
                params: { query }
            });
        } catch (err) {
            console.error("Error searching jobs:", err);
            return [];
        }
    }

    return {
        cache,
        loading,
        fetchJobs,
        fetchJobById,
        createJob,
        updateJob,
        deleteJob,
        searchJobs
    };
});
