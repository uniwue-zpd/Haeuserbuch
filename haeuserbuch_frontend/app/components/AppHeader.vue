<script setup lang="ts">
import 'primeicons/primeicons.css';
import { ref } from 'vue';

const { user, loggedIn, clear } = useUserSession();

const op = ref();
const toggle = (event: any) => op.value.toggle(event)

const logout = async () => {
  await $fetch('/auth/logout', {
    method: 'POST'
  });
  await clear();
}
</script>

<template>
  <header class="bg-[#2C3E50]">
    <div class="flex flex-row justify-between items-center max-w-[1140px] mx-auto p-3">
      <NuxtLink to="/">
        <img src="/wue_haeuserbuch_logo_skyline_white.svg" alt="Häuserbuch-Logo" class="h-[55px] w-auto"/>
      </NuxtLink>
      <div class="flex flex-row items-center space-x-2">
        <NuxtLink to="/project" class="text-white roboto-plain">Projekt</NuxtLink>
        <NuxtLink to="/contact" class="text-white roboto-plain">Kontakt</NuxtLink>
        <div class="card flex justify-center align-middle text-white">
          <Button type="button" icon="pi pi-user" rounded aria-label="User" variant="link" class="text-white" @click="toggle"/>
          <Popover ref="op" class="w-70">
            <div class="p-3">
              <div v-if="loggedIn" class="flex flex-col gap-3">
                <div class="flex items-center gap-3">
                  <div class="relative w-10 h-10">
                    <div class="w-10 h-10 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">
                      {{ (user?.name || 'U').charAt(0).toUpperCase() }}
                    </div>
                    <span class="absolute -top-0.5 -right-0.5 w-3 h-3 bg-green-500 border-2 border-white rounded-full"/>
                  </div>
                  <div class="flex flex-col">
                    <div class="font-semibold text-gray-900">
                      {{ user?.name || 'Häuserbuch-Mitglied' }}
                    </div>
                    <div class="text-xs text-gray-500">
                      Angemeldet
                    </div>
                  </div>
                </div>
                <hr class="border-gray-200"/>
                <button
                    @click="logout"
                    class="w-full flex items-center justify-center gap-2 p-2 rounded-md bg-red-100 text-red-600 hover:bg-red-200 transition font-medium cursor-pointer shadow-md hover:shadow-lg"
                >
                  <i class="pi pi-sign-out"/>
                  Abmelden
                </button>
              </div>
              <div v-else class="flex flex-col gap-3">
                <div class="text-sm font-semibold text-gray-900">Willkommen</div>
                <div class="text-xs text-gray-500 leading-snug">
                  Melden Sie sich an, um erweiterten Zugriff auf das Projekt zu erhalten.
                </div>
                <hr class="border-gray-200"/>
                <a
                    href="/auth/login"
                    class="w-full flex items-center justify-center gap-2 p-2 rounded-md bg-blue-500 text-white hover:bg-blue-600 transition font-medium cursor-pointer shadow-md hover:shadow-lg"
                >
                  <i class="pi pi-user"></i>
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
