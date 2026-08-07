<script setup lang="ts">

</script>

<template>
  <div class="flex flex-col gap-4 p-4">
    <div class="h-10 bg-gray-300 rounded-md w-1/3 animate-pulse"/>
    <div class="h-[40vh] bg-gray-300 rounded-md w-full animate-pulse"/>
    <div class="flex flex-col gap-2">
      <div v-for="i in 10" :key="i" class="h-8 bg-gray-300 w-full rounded-md animate-pulse"/>
    </div>
  </div>
</template>
