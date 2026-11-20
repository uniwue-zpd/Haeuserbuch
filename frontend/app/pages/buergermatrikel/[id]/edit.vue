<script setup lang="ts">
import CitizenshipForm from "~/components/forms/CitizenshipForm.vue";

definePageMeta({
  middleware: 'auth',
});

useHead(() => ({
  title: 'Bürgermatrikel bearbeiten'
}));

const route = useRoute();
const citizenship_id = Number(route.params.id);
const citizenshipApi = useCitizenships();

const { data: citizenshipItem, status } = await useAsyncData(`citizenship-${ citizenship_id }`, () => citizenshipApi.getCitizenship(citizenship_id));
</script>

<template>
  <CitizenshipForm header="Bürgermatrikel bearbeiten" action="edit" :citizenship="citizenshipItem ?? undefined"/>
</template>
