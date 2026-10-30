<script setup lang="ts">
const props = defineProps<{
  origin: PersonOrigin;
}>();

const originCertainty: Record<string, { label: string; color: string }> = {
  IDENTIFIED: { label: "Identifiziert", color: "bg-green-400" },
  AMBIGUOUS: { label: "Mehrdeutig", color: "bg-yellow-400" },
  UNKNOWN: { label: "Unbekannt", color: "bg-red-400" },
};
</script>

<template>
  <div class="flex w-full flex-col gap-3 rounded-md border border-default p-3 text-highlighted">
    <div
      v-if="props.origin.originalText"
      class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm first:border-t-0 first:pt-0"
    >
      <span class="font-semibold text-muted">Eingetragener Ort</span>
      <span>{{ props.origin.originalText }}</span>
    </div>
    <div
      v-if="props.origin.places.length > 0"
      class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm first:border-t-0 first:pt-0"
    >
      <span class="font-semibold text-muted">Möglicherweise</span>
      <div class="flex flex-wrap gap-x-3 gap-y-1">
        <NuxtLink
          v-for="place in props.origin.places"
          :key="place.id"
          :to="`/orte/${place.id}`"
          class="font-semibold text-highlighted underline decoration-muted underline-offset-4 hover:decoration-current focus-visible:outline-none"
        >
          {{ place.realName }}
        </NuxtLink>
      </div>
    </div>
    <div
      v-if="props.origin.certainty"
      class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm first:border-t-0 first:pt-0"
    >
      <span class="font-semibold text-muted">Lokalisierbarkeit</span>
      <div>
        <span
          :class="`border border-gray-400 px-1.5 py-0.5 rounded-md text-xs font-bold uppercase tracking-wide shadow-sm ${originCertainty[props.origin.certainty]?.color}`"
        >
          {{ originCertainty[props.origin.certainty]?.label }}
        </span>
      </div>
    </div>
  </div>
</template>
