<script setup lang="ts">
/** Original job text, optionally followed by the normalized job category */
const props = withDefaults(defineProps<{
  job: PersonJob | null | undefined;
  showNormalized: boolean;
  /** Hide the normalized category if it equals the original text (case-insensitive) */
  hideIfEqual?: boolean;
}>(), { hideIfEqual: true });

const normalized = computed(() => {
  const name = props.job?.jobCategory?.name;
  if (!props.showNormalized || !name) return null;
  if (!props.hideIfEqual) return name;
  return name.trim().toLowerCase() !== props.job?.originalText?.trim().toLowerCase() ? name : null;
});
</script>

<template>
  <div v-if="job?.originalText || normalized" class="flex flex-wrap items-baseline gap-x-1.5">
    <span v-if="job?.originalText">{{ job.originalText }}</span>
    <UBadge
      v-if="normalized"
      :label="normalized"
      color="neutral"
      variant="soft"
      size="sm"
      title="Normalisierte Form"
    />
  </div>
  <slot v-else name="empty" />
</template>
