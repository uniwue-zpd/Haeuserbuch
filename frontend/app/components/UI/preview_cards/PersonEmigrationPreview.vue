<script setup lang="ts">
const props = defineProps<{
  emigration: PersonEmigration;
}>();
</script>

<template>
  <div class="flex w-full flex-col gap-3 rounded-md border border-default p-3 text-highlighted">
    <div
      v-if="props.emigration.originalText"
      class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm first:border-t-0 first:pt-0"
    >
      <span class="font-semibold text-muted">Eingetragener Ort</span>
      <span>{{ props.emigration.originalText }}</span>
    </div>
    <div
      v-if="props.emigration.place"
      class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm first:border-t-0 first:pt-0"
    >
      <span class="font-semibold text-muted">Wegzugsort</span>
      <NuxtLink
        :to="`/orte/${props.emigration.place.id}`"
        class="font-semibold text-highlighted underline decoration-muted underline-offset-4 hover:decoration-current focus-visible:outline-none"
      >
        {{ props.emigration.place.realName }}
      </NuxtLink>
    </div>
  </div>
</template>
