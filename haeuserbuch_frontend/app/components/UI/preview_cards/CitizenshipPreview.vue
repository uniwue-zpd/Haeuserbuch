<script setup lang="ts">
const props = defineProps<{
  citizenship: CitizenshipPreviewDTO;
}>();
</script>

<template>
  <NuxtLink
    :to="`/citizenships/${props.citizenship.id}`"
    class="flex flex-col gap-3 rounded-lg border border-default p-3 text-highlighted transition-colors hover:border-accented hover:text-primary focus-visible:border-accented focus-visible:outline-none"
  >
    <span class="text-sm font-semibold text-highlighted">Bürgermatrikel</span>
    <span class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm">
      <span class="text-xs font-semibold text-muted">Signatur</span>
      <span>{{ props.citizenship.signature || "unbekannt" }}</span>
    </span>
    <span class="grid grid-cols-[minmax(7rem,9rem)_minmax(0,1fr)] gap-x-3 border-t border-default pt-2 text-sm">
      <span class="text-xs font-semibold text-muted">Referenznummer</span>
      <span>{{ props.citizenship.refNumber || "unbekannt" }}</span>
    </span>
  </NuxtLink>
</template>
