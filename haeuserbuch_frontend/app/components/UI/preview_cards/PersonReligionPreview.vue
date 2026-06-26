<script setup lang="ts">
const props = defineProps<{
  religion: PersonReligion;
}>();
</script>

<template>
  <div class="inline-block">
    <div class="flex flex-col gap-2 rounded-md shadow-md p-3 bg-gray-200 border border-gray-300">
      <div class="flex flex-row space-x-2 items-center">
        <span class="text-sm font-semibold uppercase tracking-wide text-gray-600">Eingetragene Religion:</span>
        <span class="italic text-sm">{{ props.religion.originalText }}</span>
      </div>
      <hr class="border-black"/>
      <div v-if="props.religion.religionCategory" class="flex flex-row space-x-2 items-center">
        <span class="text-sm font-semibold uppercase tracking-wide text-gray-600">Kategorie:</span>
        <NuxtLink
            :to="`/religions/${ props.religion.religionCategory.id }`"
            class="p-1.5 rounded-md bg-gray-300 text-sm font-medium shadow-sm hover:shadow-md"
        >
          {{ props.religion.religionCategory.name }}
        </NuxtLink>
      </div>

    </div>
  </div>
</template>

<style scoped>

</style>
