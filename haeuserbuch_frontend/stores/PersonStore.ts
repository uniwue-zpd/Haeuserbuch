import apiClient from "~/service/api";

export const usePersonStore = defineStore("person", () => {
    // State
    const persons = ref<Person[]>([]);
    const current_person = ref<Person | null>(null);

    // Getters
    const isLoaded = computed(() => persons.value.length > 0);

    // Actions
        // Fetch persons from the API
    async function fetchPersons() {
        if (!isLoaded.value) {
            try {
                const response = await apiClient.get<Person[]>("/persons");
                persons.value = response.data;
            } catch (error) {
                console.error("Error fetching persons:", error);
            }
        }
    }

        // Fetch person by ID
    async function fetchPersonById(id: number) {
        if (!current_person.value || current_person.value.id !== id) {
            const cachedPerson = persons.value.find(person => person.id === id);
            if (cachedPerson) {
                current_person.value = cachedPerson;
            } else {
                try {
                    const response = await apiClient.get<Person>(`/persons/${id}`);
                    current_person.value = response.data;
                } catch (error) {
                    console.error("Error fetching person by ID:", error);
                }
            }
        }
    }

        // Create new person
    async function createPerson(payload: Partial<Person>) {
        try {
            const response = await apiClient.post<Person>('/persons', payload);
            persons.value.push(response.data);
            return response.data;
        } catch (error) {
            console.error("Error creating person:", error);
            throw error;
        }
    }

        // Update existing person
    async function updatePerson(payload: Partial<Person>, id: number) {
        try {
            if (!persons.value.length) {
                console.error("Persons data is not loaded");
                return;
            }
            const response = await apiClient.put<Person>(`/persons/${id}`, payload);
            const index = persons.value.findIndex(person => person.id === id);
            if (index !== -1) {
                persons.value[index] = response.data;
            }
            return response.data;
        } catch (error) {
            console.error("Error updating person:", error);
            throw error;
        }
    }

        // Delete person
    async function deletePerson(id: number) {
        try {
            await apiClient.delete(`/persons/${id}`);
            persons.value = persons.value.filter(person => person.id !== id);
            if (current_person.value?.id === id) {
                current_person.value = null;
            }
        } catch (error) {
            console.error("Error deleting person:", error);
            throw error;
        }
    }

        // Clear current person
    function clearCurrentPerson() {
        current_person.value = null;
    }

    return {
        persons,
        current_person,
        fetchPersons,
        fetchPersonById,
        createPerson,
        updatePerson,
        deletePerson,
        clearCurrentPerson
    }
});