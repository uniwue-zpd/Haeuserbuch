export const useJobs = () => {

    /**
     * Clears cached `useAsyncData` entries containing job data, so pages refetch after create/update/delete.
     * Covers "jobs" and "job-{id}-…".
     */
    const clearJobData = () =>
        clearNuxtData((key) => key === "jobs" || key.startsWith("job-"));

    /**
     * `GET` Fetches all jobs.
     * Use it with the `useAsyncData` key "jobs", which is cleared after create/update/delete.
     * @returns Promise resolving to an array of {@link Job} entities
     */
    const getJobs = async (): Promise<Job[]> => {
        return await $fetch<Job[]>("/api/jobs");
    };

    /**
     * `GET` Fetches a single job by its ID.
     * @param id Unique identifier of the job
     * @returns Promise resolving to the {@link Job} entity
     */
    const getJob = async (id: number): Promise<Job> => {
        return await $fetch<Job>(`/api/jobs/${id}`);
    };

    /**
     * `POST` Creates a new job.
     * @param payload Partial job data to create
     * @returns Promise resolving to the created {@link Job} entity
     */
    const createJob = async (payload: Partial<Job>): Promise<Job> => {
        const data = await $fetch<Job>("/api/jobs", { method: "POST", body: payload });
        clearJobData();
        return data;
    };

    /**
     * `PUT` Updates an existing job.
     * @param id ID of the job to update
     * @param payload Partial job data to update
     * @returns Promise resolving to the updated {@link Job} entity
     */
    const updateJob = async (id: number, payload: Partial<Job>): Promise<Job> => {
        const data = await $fetch<Job>(`/api/jobs/${id}`, { method: "PUT", body: payload });
        clearJobData();
        return data;
    };

    /**
     * `DELETE` Deletes a job by its ID.
     * @param id ID of the job to delete
     */
    const deleteJob = async (id: number): Promise<void> => {
        await $fetch(`/api/jobs/${id}`, { method: "DELETE" });
        clearJobData();
    };

    /**
     * `GET` Searches jobs by name.
     * @param query Search term
     * @returns Promise resolving to an array of {@link JobDTO} matching the query
     */
    const searchJobs = async (query: string): Promise<JobDTO[]> => {
        return await $fetch<JobDTO[]>("/api/jobs/search", { query: { query } });
    };

    /**
     * `GET` a page of the occupation register: occupations with their related persons or citizenship entries,
     * sorted by name and optionally filtered by name or alternative name.
     * Use it with the `useAsyncData` key "job-register-{type}", which is cleared after create/update/delete.
     * @param params Register type, name filter and paging.
     * @returns A page of occupations with their entries.
     */
    const getJobRegister = async <T>(params: FilterRegister): Promise<Page<RegisterEntry<T>>> => {
        return await $fetch<Page<RegisterEntry<T>>>("/api/jobs/register", { query: params });
    };

    return {
        getJobRegister,
        getJobs,
        getJob,
        createJob,
        updateJob,
        deleteJob,
        searchJobs
    };
};
