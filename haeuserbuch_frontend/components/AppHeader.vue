<script setup lang="ts">
import 'primeicons/primeicons.css';
import { ref } from 'vue';

const op = ref();
const toggle = (event: any) => op.value.toggle(event)
</script>

<template>
  <header class="bg-[#2C3E50]">
    <div class="flex flex-row justify-between items-center max-w-[1140px] mx-auto p-3">
      <NuxtLink to="/">
        <img src="../public/WUE_LOGO_Skyline_260408_HQ.svg" alt="Häuserbuch-Logo" class="h-[40px] w-auto"/>
      </NuxtLink>
      <div class="flex flex-row items-center space-x-2">
        <NuxtLink to="/project" class="text-white roboto-plain">Projekt</NuxtLink>
        <NuxtLink to="/contact" class="text-white roboto-plain">Kontakt</NuxtLink>
        <div class="card flex justify-center align-middle text-white">
          <Button type="button" icon="pi pi-user" rounded aria-label="User" variant="link" class="text-white" @click="toggle"/>
          <Popover ref="op">
            <div class="card flex justify-center">
              <div>Hier entsteht der Anmeldebereich</div>
            </div>
          </Popover>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.p-button-link {
  color: white;
}

.p-button-link:not(:disabled):hover {
  color: white;
}
</style>
