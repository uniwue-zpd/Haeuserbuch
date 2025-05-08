<script setup lang="ts">
import 'primeicons/primeicons.css';
import { ref } from 'vue';

const op = ref();
const toggle = (event: any) => op.value.toggle(event)
</script>

<template>
  <header class="bg-[#ADA587]">
    <div class="flex flex-row justify-between items-center max-w-[1140px] mx-auto p-3">
      <NuxtLink to="/">
        <div class="text-xl font-bold">Häuserbuch-Logo</div>
      </NuxtLink>
      <div class="flex flex-row items-center space-x-2">
        <NuxtLink to="/project" class="text-black">Projekt</NuxtLink>
        <NuxtLink to="/contact" class="text-black">Kontakt</NuxtLink>
        <div class="card flex justify-center align-middle text-black">
          <Button type="button" icon="pi pi-user" rounded aria-label="User" variant="link" class="text-black" @click="toggle"/>
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
  color: black;
}

.p-button-link:not(:disabled):hover {
  color: black;
}
</style>
