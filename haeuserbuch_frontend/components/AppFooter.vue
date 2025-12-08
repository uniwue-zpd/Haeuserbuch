<script setup lang="ts">
import { ref } from "vue";
import { useToast } from "primevue/usetoast";

const toast = useToast();
const email = 'email@uni-wuerzburg.de';
const date = ref(new Date().getFullYear());

function copy_to_clipboard(copy_item: string) {
  navigator.clipboard.writeText(copy_item)
  toast.add({detail: 'In die Zwischenablage kopiert', life: 3000})
}
</script>

<template>
  <footer class="flex flex-col">
    <div class="bg-[#2C3E50]">
      <div class="flex flex-col gap-2 md:flex-row md:justify-between max-w-[1140px] text-black p-3 mx-auto">
        <div class="flex flex-row justify-between md:space-x-10">
          <div class="flex flex-col gap-2">
            <p class="text-xl montserrat-headline font-bold text-white">Häuserbuch</p>
            <NuxtLink to="/project" class="text-white roboto-plain">Projekt</NuxtLink>
            <NuxtLink to="/faq" class="text-white roboto-plain">FAQ</NuxtLink>
            <NuxtLink to="/team" class="text-white roboto-plain">Über uns</NuxtLink>
          </div>
          <div class="flex flex-col gap-2">
            <p class="text-xl montserrat-headline font-bold text-white">Kontakt</p>
            <div class="space-x-2">
              <Toast />
              <a v-bind:href="'mailto:' + email" class="text-white roboto-plain">{{ email }}</a>
              <i class="pi pi-clone hover:cursor-pointer text-white" @click="copy_to_clipboard(email)"></i>
            </div>
            <NuxtLink to="/contact" class="text-white roboto-plain">Kontaktformular</NuxtLink>
          </div>
        </div>
        <div class="flex flex-row justify-between md:space-x-10 md:justify-end">
          <div class="flex flex-col justify-center">
            <a href="https://www.dfg.de/de" target="_blank">
              <img class="h-[30px] md:h-[40px]" src="../public/dfg_logo_schriftzug_weiss.png" alt="dfglogo"/>
            </a>
          </div>
          <div class="flex flex-col justify-center">
            <a href="https://www.uni-wuerzburg.de/" target="_blank">
              <img class="h-[30px] md:h-[40px]" src="../public/unilogo4cohne_w.jpg" alt="unilogo"/>
            </a>
          </div>
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
