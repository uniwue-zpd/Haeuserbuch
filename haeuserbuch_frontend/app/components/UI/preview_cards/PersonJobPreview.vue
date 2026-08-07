<script setup lang="ts">
const props = defineProps<{
  job: PersonJob;
}>();
</script>

<template>
  <div class="flex flex-col gap-[0.65rem] border-t border-muted py-3">
    <div v-if="props.job.originalText" class="flex flex-wrap items-baseline gap-3 text-[0.9rem] text-highlighted">
      <span class="text-xs font-bold text-muted">Eingetragener Beruf</span>
      <span>{{ props.job.originalText }}</span>
    </div>
    <div v-if="props.job.jobCategory" class="flex flex-wrap items-baseline gap-3 text-[0.9rem] text-highlighted">
      <span class="text-xs font-bold text-muted">Kategorie</span>
      <NuxtLink :to="`/jobs/${props.job.jobCategory.id}`" class="font-[650] text-highlighted underline underline-offset-[0.2rem] hover:text-[var(--ui-primary)] focus-visible:text-[var(--ui-primary)] focus-visible:outline-none">
        {{ props.job.jobCategory.name }}
      </NuxtLink>
    </div>
  </div>
</template>
