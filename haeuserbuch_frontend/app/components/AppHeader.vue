<script setup lang="ts">
import 'primeicons/primeicons.css';
import { ref } from 'vue';

const op = ref();
const toggle = (event: any) => op.value.toggle(event)
const { user, loggedIn } = useUserSession();
const getKeycloak = () => {
  navigateTo('/auth/keycloak')
}
</script>

<template>
  <header class="bg-[#2C3E50]">
    <div class="flex flex-row justify-between items-center max-w-[1140px] mx-auto p-3">
      <NuxtLink to="/">
        <img src="/WUE_LOGO_Skyline_260408_HQ.svg" alt="Häuserbuch-Logo" class="h-[40px] w-auto"/>
      </NuxtLink>
      <div class="flex flex-row items-center space-x-2">
        <NuxtLink to="/project" class="text-white roboto-plain">Projekt</NuxtLink>
        <NuxtLink to="/contact" class="text-white roboto-plain">Kontakt</NuxtLink>
        <div class="card flex justify-center align-middle text-white">
          <Button type="button" icon="pi pi-user" rounded aria-label="User" variant="link" class="text-white" @click="toggle"/>
          <Popover ref="op">
            <div v-if="loggedIn" class="flex flex-col gap-3 p-3">
              <div class="font-bold montserrat-headline">Willkommen, {{ user?.name || 'Häuserbuch-Mitglied' }}</div>
              <div>
                <a
                    href="/auth/logout"
                    class="p-1.5 bg-blue-300 border border-gray-400 rounded-md shadow-md font-bold roboto-plain"
                >
                  Abmelden
                </a>
              </div>
            </div>
            <div v-else class="flex flex-col gap-3 p-3">
              <div class="font-bold montserrat-headline">Sind Sie Projektmitglied?</div>
              <div>
                <a
                    href="/auth/logout"
                    class="p-1.5 bg-blue-300 border border-gray-400 rounded-md shadow-md font-bold roboto-plain"
                >
                  Anmelden
                </a>
              </div>
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
