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
                const { data } = await useFetch("/api/persons");
                persons.value = data.value as Person[];
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
                    const { data } = await useFetch(`/api/persons/${id}`);
                    current_person.value = data.value as Person;
                } catch (error) {
                    console.error("Error fetching person by ID:", error);
                }
            }
        }
    }

        // Create new person
    async function createPerson(payload: Partial<Person>) {
        try {
            const { data } = await useFetch('/api/persons', {
                method: 'POST',
                body: payload,
            });
            persons.value.push(data.value as Person);
            return data.value;
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
            const { data } = await useFetch<Person>(`/api/persons/${id}`, {
                method: 'PUT',
                body: payload,
            });
            const index = persons.value.findIndex(person => person.id === id);
            if (index !== -1) {
                persons.value[index] = data.value as Person;
            }
            if (current_person.value?.id === id) {
                current_person.value = data.value as Person;
            }
            return data.value;
        } catch (error) {
            console.error("Error updating person:", error);
            throw error;
        }
    }

        // Delete person
    async function deletePerson(id: number) {
        if (!persons.value) {
            console.error("Persons data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/persons/${id}`, {
            method: 'DELETE'
        });
        if (error.value) {
            console.error('Error deleting person:', error.value);
            throw error.value;
        }
        persons.value = persons.value.filter(p => p.id !== id);
        if (current_person.value?.id === id) {
            current_person.value = null;
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
