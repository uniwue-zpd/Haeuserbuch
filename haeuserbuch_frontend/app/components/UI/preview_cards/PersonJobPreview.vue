<script setup lang="ts">
const props = defineProps<{
  job: PersonJob;
}>();
</script>

<template>
  <div class="inline-block">
    <div class="flex flex-col gap-2 rounded-md shadow-md p-3 border-2 border-gray-300">
      <div class="flex flex-row space-x-3 items-center">
        <span class="text-sm font-semibold uppercase tracking-wide text-gray-500">Eingetragener Beruf:</span>
        <span class="italic text-sm">{{ props.job.originalText }}</span>
      </div>
      <hr class="border"/>
      <div v-if="props.job.jobCategory" class="flex flex-row space-x-3 items-center">
        <span class="text-sm font-semibold uppercase tracking-wide text-gray-500">Kategorie:</span>
        <NuxtLink
            :to="`/jobs/${ props.job.jobCategory.id }`"
            class="p-1.5 border-2 border-gray-300 rounded-lg shadow-sm hover:shadow-md font-semibold"
        >
          {{ props.job.jobCategory.name }}
        </NuxtLink>
      </div>

    </div>
  </div>
</template>

<style scoped>

</style>
