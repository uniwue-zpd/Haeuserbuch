<script setup lang="ts">

</script>

<template>
  <div class="mx-auto mt-12 max-w-2xl overflow-hidden rounded-xl border border-gray-200 bg-white shadow-lg">
    <div class="border-b border-gray-200 bg-gray-50 px-8 py-10">
      <div class="flex flex-col items-center gap-5">
        <div class="flex h-24 w-24 items-center justify-center rounded-full border border-blue-200 bg-blue-50 shadow-sm">
          <Icon
              name="material-symbols:encrypted-outline-rounded"
              class="text-6xl text-blue-600"
          />
        </div>
        <div class="text-center">
          <h2 class="montserrat-headline text-2xl font-bold text-gray-900">Geschützter Bereich</h2>
          <p class="mt-2">Dieser Bereich ist nur für angemeldete Benutzer zugänglich.</p>
        </div>
      </div>
    </div>
    <div class="flex flex-col items-center gap-4 p-6">
      <div class="w-full rounded-lg border border-gray-200 bg-gray-50 p-5 text-center">
        <p class="text-base">
          Bitte melden Sie sich mit Ihrem Konto an, um diese Seite aufzurufen.
        </p>
      </div>
      <a
          href="/auth/login"
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 p-2 font-semibold text-white shadow-sm transition-colors duration-200 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
      >
        <Icon
            name="material-symbols:login-rounded"
            class="text-xl"
        />
        <span>Anmelden</span>
      </a>
    </div>
  </div>
</template>

<style scoped>

</style>
