import type {Job} from "~/utils/types";

export const useJobStore = defineStore("job", () => {
    // State
    const jobs = ref<Job[]>([]);
    const currentJob = ref<Job | null>(null);

    // Getters
    const isLoaded = computed(() => jobs.value.length > 0);

    // Actions

    /**
     * GET all jobs from the API and store them in the `jobs` array.
     * Only fetches if the data is not already loaded (checked via `isLoaded` getter).
     */
    async function fetchJobs() {
        if (!isLoaded.value) {
            const { data, error } = await useFetch("/api/jobs");
            if (error.value) {
                console.error("Error fetching jobs:", error.value);
                return;
            }
            jobs.value = data.value as Job[];
        }
    }

    /**
     * GET job by ID. First checks if the job is already cached in the `jobs` array.
     * @param id ID of the job to fetch
     */
    async function fetchJobById(id: number) {
        if (!currentJob.value || currentJob.value.id !== id) {
            const cachedItem = jobs.value.find(job => job.id === id);
            if (cachedItem) {
                currentJob.value = cachedItem;
            } else {
                try {
                    currentJob.value = await $fetch<Job>(`/api/jobs/${id}`);
                } catch (err) {
                    console.error(`Error fetching job by ID: ${ id }`, err);
                    return;
                }
            }
        }
    }

    /**
     * POST Create a new job using the given payload.
     * On success, adds the new job to the `jobs` array.
     * @param payload Partial job data to create
     */
    async function createJob(payload: Partial<Job>) {
        try {
            const newJob = await $fetch<Job>('/api/jobs', {
                method: 'POST',
                body: payload,
            });
            jobs.value.push(newJob);
            return newJob;
        } catch (err) {
            console.error("Error creating job:", err);
            return;
        }
    }

    /**
     * PUT Update an existing job by ID using the given payload.
     * On success, updates the corresponding job in the `jobs` array.
     * @param payload Partial job data to update
     * @param id ID of the job to update
     */
    async function updateJob(payload: Partial<Job>, id: number) {
        if (jobs.value.length === 0) {
            console.error("jobs data is not loaded");
            return;
        }
        try {
            const updatedJob = await $fetch<Job>(`/api/jobs/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = jobs.value.findIndex(job => job.id === id);
            if (index !== -1) jobs.value[index] = updatedJob;
            if (currentJob.value && currentJob.value.id === id) currentJob.value = updatedJob;
            return updatedJob;
        } catch (err) {
            console.error("Error updating job:", err);
            return;
        }
    }

    /**
     * DELETE a job by ID.
     * On success, removes the job from the `jobs` array.
     * @param id ID of the job to delete
     */
    async function deleteJob(id: number) {
        if (!jobs.value) {
            console.error("jobs data is not loaded");
            return;
        }
        try {
            await $fetch(`/api/jobs/${id}`, { method: 'DELETE' });
            jobs.value = jobs.value.filter(job => job.id !== id);
            if (currentJob.value && currentJob.value.id === id) currentJob.value = null;
        } catch (err) {
            console.error('Error deleting job:', err);
            return;
        }
    }

    return {
        jobs,
        currentJob,
        isLoaded,
        fetchJobs,
        fetchJobById,
        createJob,
        updateJob,
        deleteJob
    };
});
