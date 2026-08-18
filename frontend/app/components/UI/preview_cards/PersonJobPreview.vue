<script setup lang="ts">
const props = defineProps<{
  job: PersonJob;
}>();
</script>

<template>
  <div class="flex w-full flex-col gap-3 rounded-md border border-default p-3 text-highlighted">
    <div
      v-if="props.job.originalText"
      class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm first:border-t-0 first:pt-0"
    >
      <span class="font-semibold text-muted">Eingetragener Beruf</span>
      <span>{{ props.job.originalText }}</span>
    </div>
    <div
      v-if="props.job.jobCategory"
      class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm"
    >
      <span class="font-semibold text-muted">Kategorie</span>
      <NuxtLink
        :to="`/jobs/${props.job.jobCategory.id}`"
        class="font-semibold text-highlighted underline decoration-muted underline-offset-4 hover:decoration-current focus-visible:outline-none"
      >
        {{ props.job.jobCategory.name }}
      </NuxtLink>
    </div>
  </div>
</template>
