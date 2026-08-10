<script setup lang="ts">
const props = defineProps<{
  person: PersonPreviewDTO;
}>();
</script>

<template>
  <div>
    <NuxtLink
        :to="`/persons/${ props.person.id }`"
        class="rounded-lg shadow-sm hover:shadow-md p-1.5 font-medium border-2 border-gray-300"
    >
      {{ person.fullName }}
    </NuxtLink>
  </div>
</template>
