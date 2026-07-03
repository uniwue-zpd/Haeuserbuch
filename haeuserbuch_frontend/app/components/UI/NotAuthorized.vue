<script setup lang="ts">

</script>

<template>
  <div class="md:w-1/2 mx-auto mt-10 rounded-xl border border-red-200 bg-white shadow-lg overflow-hidden">
    <div class="bg-red-50 border-b border-red-200 p-6">
      <div class="flex flex-col items-center gap-4">
        <div class="flex h-24 w-24 items-center justify-center rounded-full bg-white border border-red-300 shadow-sm">
          <Icon
              name="material-symbols:encrypted-outline-rounded"
              class="text-black text-6xl"
          />
        </div>
        <p class="montserrat-headline text-xl font-bold text-black">Geschützter Bereich</p>
      </div>
    </div>
    <div class="p-6">
      <p class="rounded-lg border border-gray-200 bg-gray-50 p-4 text-lg text-center font-mono">
        Melden Sie sich an, um diesen Bereich zu sehen
      </p>
    </div>
  </div>
</template>

<style scoped>

</style>
