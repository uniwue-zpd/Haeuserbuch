<script setup lang="ts">
const props = defineProps<{
  citizenship: CitizenshipPreviewDTO;
}>();
</script>

<template>
  <div class="inline-block">
    <NuxtLink :to="`/citizenships/${ props.citizenship.id }`">
      <div class="w-full p-4 rounded-md border-2 border-gray-300 shadow-md hover:shadow-xl hover:translate-x-1 transition-all duration-200">
        <div class="flex flex-row items-center gap-4">
          <div class="flex flex-col gap-2 w-full">
            <div class="flex justify-end">
            <span class="text-xs uppercase tracking-wide bg-gray-100 p-1.5 rounded-md text-gray-600">
              Bürgermatrikel
            </span>
            </div>
            <hr class="border"/>
            <div class="flex flex-col gap-1 text-sm">
              <div class="flex flex-row gap-2">
                <span class="font-semibold">Signatur:</span>
                <span>{{ props.citizenship.signature || 'unbekannt' }}</span>
              </div>
              <div class="flex flex-row gap-2">
                <span class="font-semibold">Referenznummer:</span>
                <span>{{ props.citizenship.refNumber || 'unbekannt' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </NuxtLink>
  </div>
</template>

<style scoped>

</style>
