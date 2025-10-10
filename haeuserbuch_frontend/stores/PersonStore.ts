export const usePersonStore = defineStore("person", () => {
    // State
    const persons = ref<Person[]>([]);
    const current_person = ref<Person | null>(null);

    // Getters
    const isLoaded = computed(() => persons.value !== null);

    // Actions
        // Fetch persons from the API
    async function fetchPersons() {
        if (!isLoaded.value) {
            const { data, error } = await useFetch("/api/persons");
            if (error.value) {
                console.error("Error fetching persons:", error.value);
                return;
            }
            persons.value = data.value as Person[];
        }
    }

        // Fetch person by ID
    async function fetchPersonById(id: number) {
        if (!current_person.value || current_person.value.id !== id) {
            const cachedPerson = persons.value.find(person => person.id === id);
            if (cachedPerson) {
                current_person.value = cachedPerson;
            } else {
                const { data, error } = await useFetch(`/api/persons/${id}`);
                if (error.value) {
                    console.error(`Error fetching person by ID: ${ id }`, error.value);
                    return;
                }
                current_person.value = data.value as Person;
            }
        }
    }

        // Create new person
    async function createPerson(payload: Partial<Person>) {
        const { data, error } = await useFetch('/api/persons', {
            method: 'POST',
            body: payload,
        });
        if (error.value) {
            console.error("Error creating person:", error.value);
            return;
        }
        persons.value.push(data.value as Person);
        return data.value;
    }

        // Update existing person
    async function updatePerson(payload: Partial<Person>, id: number) {
        if (!persons.value.length) {
            console.error("Persons data is not loaded");
            return;
        }
        const { data, error } = await useFetch<Person>(`/api/persons/${id}`, {
            method: 'PUT',
            body: payload,
        });
        if (error.value) {
            console.error("Error updating person:", error.value);
            return;
        }
        const updatedPerson = data.value as Person;
        const index = persons.value.findIndex(person => person.id === id);
        if (index !== -1) persons.value[index] = updatedPerson;
        if (current_person.value?.id === id) current_person.value = updatedPerson;
        return updatedPerson;
    }

        // Delete person
    async function deletePerson(id: number) {
        if (!persons.value) {
            console.error("Persons data is not loaded");
            return;
        }
        const { error } = await useFetch(`/api/persons/${id}`, { method: 'DELETE' });
        if (error.value) {
            console.error('Error deleting person:', error.value);
            return;
        }
        persons.value = persons.value.filter(p => p.id !== id);
        if (current_person.value?.id === id) current_person.value = null;
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
