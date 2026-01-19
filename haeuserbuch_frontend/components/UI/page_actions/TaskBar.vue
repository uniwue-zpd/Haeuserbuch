<script setup lang="ts">
import { useConfirm } from 'primevue/useconfirm';
import { PROJECT_DOMAIN } from "~/utils/constant_values";

const props = defineProps<{
  id: number;
  entity_type: 'buildings' | 'persons' | 'places' | 'sources';
}>();

const confirm = useConfirm();
const toast = useToast();

const router = useRouter();
const show_toolbar = ref(false);

// Paths
const path = ref(`/${props.entity_type}/${props.id}`);
const edit_path = ref(`${ path.value }/edit`);
const api_path = ref(`/api${ path.value }`);

// Stores
const building_store = useBuildingStore();
const person_store = usePersonStore();
const place_store = usePlaceStore();

// Delete Handlers
const deleteHandlers: Record<string, (id: number) => Promise<void>> = {
  buildings: async (id: number) => { await building_store.deleteBuilding(id); },
  persons: async (id: number) => { await person_store.deletePerson(id); },
  places: async (id: number) => { await place_store.deletePlace(id); },
}

const actions = {
  edit_page: () => {
    toast.add({ severity: 'info', summary: 'Info', detail: 'Gehe zum Editor-Ansicht', life: 3000 });
  },
  api_view: () => {
    toast.add({ severity: 'info', summary: 'Info', detail: 'Gehe zur API-Ansicht', life: 3000 });
  },
  copy_url: () => {
    navigator.clipboard.writeText(`${ PROJECT_DOMAIN }${ path.value }`);
    toast.add({ severity: 'info', summary: 'Hinweis', detail: 'Seiten-Link erfolgreich kopiert!', life: 3000 });
  },
  delete_page: () => {
    confirm.require({
      message: 'Möchten Sie diesen Eintrag wirklich löschen?',
      header: 'Bestätigung erforderlich',
      icon: 'pi pi-exclamation-triangle',
      rejectProps: {
        label: 'Abbrechen',
        severity: 'secondary',
        outlined: true
      },
      acceptProps: {
        label: 'Löschen'
      },
      accept: async () => {
        try {
          await deleteHandlers[props.entity_type](props.id);
          toast.add({ severity: 'info', summary: 'Bestätigung', detail: 'Löschvorgang erfolgreich', life: 3000 });
          await router.push(props.entity_type);
        } catch (err) {
          toast.add({ severity: 'error', summary: 'Fehler', detail: 'Löschvorgang fehlgeschlagen', life: 3000 });
        }
      },
      reject: () => {
        toast.add({ severity: 'error', summary: 'Abbruch', detail: 'Löschvorgang abgebrochen', life: 3000 });
      }
    })
  }
}
</script>

<template>
  <div class="flex items-center justify-center gap-3">
    <Transition
        name="fade"
        enter-active-class="transform transition-transform transition-opacity duration-150 ease-linear"
        enter-from-class="translate-x-3 opacity-0"
        enter-to-class="translate-x-0 opacity-100"
        leave-active-class="transform transition-transform transition-opacity duration-150 ease-linear"
        leave-from-class="translate-x-0 opacity-100"
        leave-to-class="translate-x-3 opacity-0"
    >
      <div v-if="show_toolbar" class="flex flex-row space-x-2">
        <NuxtLink
            :to="edit_path"
            class="p-1 rounded-md leading-none bg-[#f1f5f9] hover:bg-[#e2e8f0] shadow-sm"
            @click="actions.edit_page()"
            title="Eintrag bearbeiten"
        >
          <Icon name="material-symbols-edit-square-outline-sharp" class="text-xl text-black"/>
        </NuxtLink>
        <NuxtLink
            :to="api_path"
            class="p-1 rounded-md leading-none bg-[#f1f5f9] hover:bg-[#e2e8f0] shadow-sm"
            @click="actions.api_view()"
            title="API-Ansicht"
            target="_blank"
        >
          <Icon name="material-symbols-code" class="text-xl text-black"/>
        </NuxtLink>
        <button
            @click="actions.copy_url()"
            class="p-1 rounded-md leading-none bg-[#f1f5f9] hover:bg-[#e2e8f0] shadow-sm"
            title="Seiten-URL teilen"
        >
          <Icon name="material-symbols-share" class="text-xl text-black"/>
        </button>
        <ConfirmDialog/>
        <button
            @click="actions.delete_page()"
            class="p-1 rounded-md leading-none bg-[#f1f5f9] hover:bg-[#e2e8f0] shadow-sm"
            title="Eintrag löschen"
        >
          <Icon name="material-symbols-delete-outline" class="text-xl text-black"/>
        </button>
      </div>
    </Transition>
    <button
        @click="show_toolbar = !show_toolbar"
        class="p-1 leading-none rounded-md hover:shadow-md"
        title="Mehr Optionen"
    >
      <Icon name="material-symbols-more-vert" class="text-2xl"/>
    </button>
  </div>
</template>

<style scoped>
</style>
