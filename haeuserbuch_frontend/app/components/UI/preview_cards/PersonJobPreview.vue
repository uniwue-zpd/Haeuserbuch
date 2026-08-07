<script setup lang="ts">
const props = defineProps<{
  job: PersonJob;
}>();
</script>

<template>
  <div class="job-preview">
    <div v-if="props.job.originalText" class="job-row">
      <span class="preview-key">Eingetragener Beruf</span>
      <span>{{ props.job.originalText }}</span>
    </div>
    <div v-if="props.job.jobCategory" class="job-row">
      <span class="preview-key">Kategorie</span>
      <NuxtLink :to="`/jobs/${props.job.jobCategory.id}`" class="job-link">
        {{ props.job.jobCategory.name }}
      </NuxtLink>
    </div>
  </div>
</template>

<style scoped>
.job-preview {
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
  border-top: 1px solid var(--ui-border-muted);
  padding: 0.75rem 0;
}

.job-row {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 0.75rem;
  color: var(--ui-text-highlighted);
  font-size: 0.9rem;
}

.preview-key {
  color: var(--ui-text-muted);
  font-size: 0.75rem;
  font-weight: 700;
}

.job-link {
  color: var(--ui-text-highlighted);
  font-weight: 650;
  text-decoration: underline;
  text-underline-offset: 0.2rem;
}

.job-link:hover,
.job-link:focus-visible {
  color: var(--ui-primary);
  outline: none;
}
</style>
