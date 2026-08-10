<script setup lang="ts">
const props = defineProps<{
  job: PersonJob;
}>();
</script>

<template>
  <div class="flex flex-col gap-3 border-t border-muted py-3">
    <div v-if="props.job.originalText" class="flex flex-wrap items-baseline gap-3 text-sm text-highlighted">
      <span class="text-sm font-bold text-muted">Eingetragener Beruf</span>
      <span>{{ props.job.originalText }}</span>
    </div>
    <div v-if="props.job.jobCategory" class="flex flex-wrap items-baseline gap-3 text-sm text-highlighted">
      <span class="text-sm font-bold text-muted">Kategorie</span>
      <NuxtLink :to="`/jobs/${props.job.jobCategory.id}`" class="font-semibold text-highlighted underline underline-offset-4 hover:text-primary focus-visible:text-primary focus-visible:outline-none">
        {{ props.job.jobCategory.name }}
      </NuxtLink>
    </div>
  </div>
</template>
