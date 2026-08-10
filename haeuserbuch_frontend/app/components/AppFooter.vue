<script setup lang="ts">
import { ref } from "vue";
import { useToast } from "primevue/usetoast";

const toast = useToast();
const email = 'christian.naser@uni-wuerzburg.de';
const date = ref(new Date().getFullYear());

function copy_to_clipboard(copy_item: string) {
  navigator.clipboard.writeText(copy_item)
  toast.add({detail: 'In die Zwischenablage kopiert', life: 3000})
}
</script>

<template>
  <footer class="flex flex-col">
    <div class="bg-[#2C3E50]">
      <div class="flex flex-col gap-6 md:justify-between max-w-[1140px] p-3 mx-auto">
        <div class="flex flex-col md:flex-row justify-between gap-2">
          <div class="flex flex-col gap-2">
            <p class="text-lg md:text-xl montserrat-headline font-bold text-white">Häuserbuch</p>
            <NuxtLink to="/project" class="text-white roboto-plain text-sm md:text-base">Projekt</NuxtLink>
            <NuxtLink to="/faq" class="text-white roboto-plain text-sm md:text-base">FAQ</NuxtLink>
            <NuxtLink to="/team" class="text-white roboto-plain text-sm md:text-base">Über uns</NuxtLink>
          </div>
          <div class="flex flex-col gap-2">
            <p class="text-lg md:text-xl montserrat-headline font-bold text-white">Kontakt</p>
            <div class="space-x-2">
              <Toast />
              <a v-bind:href="'mailto:' + email" class="text-white roboto-plain text-sm md:text-base">{{ email }}</a>
              <i class="pi pi-clone hover:cursor-pointer text-white text-sm md:text-base" @click="copy_to_clipboard(email)"></i>
            </div>
            <NuxtLink to="/contact" class="text-white roboto-plain text-sm md:text-base">Kontaktformular</NuxtLink>
          </div>
        </div>
        <div class="flex flex-wrap items-center justify-center gap-6 md:justify-between md:gap-4">
          <a href="https://www.dfg.de/de" target="_blank" title="Deutsche Forschungsgemeinschaft">
            <img class="h-[30px] md:h-[55px]" src="/dfg.png" alt="DFG-Logo"/>
          </a>
          <a href="https://www.uni-wuerzburg.de/" target="_blank" title="Universität Würzburg">
            <img class="h-[30px] md:h-[55px]" src="/uniwue.jpg" alt="Uni-Wue-Logo"/>
          </a>
          <a href="https://www.gda.bayern.de/de/archives/staatsarchiv-kitzingen/" target="_blank" title="Staatsarchiv Kitzingen">
            <img class="h-[30px] md:h-[55px]" src="/sta.jpg" alt="Staatsarchiv-Kitzingen-Logo"/>
          </a>
          <a href="https://www.blfd.bayern.de/" target="_blank" title="Bayerisches Landesamt für Denkmalpflege">
            <img class="h-[30px] md:h-[55px]" src="/blfd.jpg" alt="BLFD-Logo"/>
          </a>
          <a href="https://www.ldbv.bayern.de/" target="_blank" title="Landesamt für Digitalisierung, Breitband und Vermessung">
            <img class="h-[30px] md:h-[55px]" src="/ldbv.svg" alt="LDBV-Logo"/>
          </a>
        </div>
      </div>
    </div>
    <div class="bg-[#F1F2F2]">
      <div class="flex flex-row max-w-[1140px] justify-between px-3 py-1 mx-auto">
        <div class="flex flex-row space-x-2">
          <div class="roboto-plain">&copy;</div>
          <div class="roboto-plain">{{ date }}</div>
        </div>
        <div class="flex space-x-2 roboto-plain">
          <NuxtLink to="/impressum">Impressum</NuxtLink>
          <div>|</div>
          <NuxtLink to="/data-protection">Datenschutz</NuxtLink>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>

</style>
