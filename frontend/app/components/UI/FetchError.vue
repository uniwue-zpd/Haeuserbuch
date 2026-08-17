<script setup lang="ts">
import type { NuxtError } from "#app";

const props = defineProps<{
  error: NuxtError;
}>();
</script>

<template>
  <div
    class="mx-auto mt-10 overflow-hidden rounded-xl border border-error/30 bg-default shadow-lg md:w-1/2"
  >
    <div class="border-b border-error/30 bg-error/10 p-6">
      <div class="flex flex-col items-center gap-3">
        <div
          class="flex h-16 w-16 items-center justify-center rounded-lg border border-error/40 bg-default font-mono text-2xl font-bold text-error shadow-sm"
        >
          {{ props.error.statusCode }}
        </div>
        <p class="text-xl font-bold text-highlighted">Ein Fehler ist aufgetreten</p>
      </div>
    </div>
    <div class="p-6">
      <p
        class="rounded-lg border border-default bg-elevated p-4 text-center font-mono text-base text-highlighted"
      >
        {{ props.error.message }}
      </p>
    </div>
  </div>
</template>
