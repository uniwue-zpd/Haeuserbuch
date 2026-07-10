<script setup lang="ts">
import type { NuxtError } from "#app";

const props = defineProps<{
  error: NuxtError;
}>();
</script>

<template>
  <div
      class="md:w-1/2 mx-auto rounded-xl border border-red-200 bg-white shadow-lg overflow-hidden mt-10"
  >
    <div class="bg-red-50 border-b border-red-200 p-6">
      <div class="flex flex-col items-center gap-3">
        <div class="flex h-16 w-16 items-center justify-center rounded-lg border border-red-300 bg-white font-mono text-2xl font-bold text-red-700 shadow-sm">
          {{ props.error.statusCode }}
        </div>
        <p class="montserrat-headline text-xl font-bold text-black">Ein Fehler ist aufgetreten</p>
      </div>
    </div>
    <div class="p-6">
      <p class="rounded-lg border border-gray-200 bg-gray-50 p-4 text-base text-center font-mono">
        {{ props.error.message }}
      </p>
    </div>
  </div>
</template>

<style scoped>

</style>
