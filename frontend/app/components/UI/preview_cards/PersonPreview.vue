<script setup lang="ts">
const props = defineProps<{
  person: PersonPreviewDTO;
}>();
</script>

<template>
  <div>
    <NuxtLink
        :to="`/personen/${ props.person.id }`"
        class="rounded-md shadow-sm hover:shadow-md p-1.5 font-medium border-2 border-gray-300"
    >
      {{ person.fullName }}
    </NuxtLink>
  </div>
</template>
