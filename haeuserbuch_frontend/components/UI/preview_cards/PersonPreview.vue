<script setup lang="ts">
const props = defineProps<{
  person: PersonPreviewDTO;
}>();
</script>

<template>
  <div class="flex flex-row items-center gap-2 flex-wrap">
    <NuxtLink
        :to="`/persons/${ props.person.id }`"
        class="rounded-md shadow-sm hover:shadow-md p-1.5 bg-gray-200 font-medium border border-gray-300"
    >
      {{ person.fullName }}
    </NuxtLink>
  </div>
</template>

<style scoped>

</style>
