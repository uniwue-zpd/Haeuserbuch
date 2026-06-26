<script setup lang="ts">

</script>

<template>
  <div class="flex flex-col gap-4 p-4">
    <div class="flex flex-row justify-between">
      <div class="h-10 bg-gray-300 rounded-md w-1/3 animate-pulse"/>
      <div class="h-10 bg-gray-300 rounded-md w-[4%] animate-pulse"/>
    </div>
    <div class="h-20 bg-gray-300 rounded-md animate-pulse"/>
    <div v-for="i in 8" :key="i">
      <div class="flex flex-row space-x-3">
        <div class="w-1/5 h-7 bg-gray-300 rounded-md animate-pulse"/>
        <div class="w-4/5 h-7 bg-gray-300 rounded-md animate-pulse"/>
      </div>
    </div>
    <div class="h-20 bg-gray-300 rounded-md animate-pulse"/>
  </div>
</template>

<style scoped>

</style>
