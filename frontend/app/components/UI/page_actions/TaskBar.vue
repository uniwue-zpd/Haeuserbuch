<script setup lang="ts">
import { useToast as useNuxtToast } from "@nuxt/ui/composables";
import { useConfirm } from "primevue/useconfirm";
import { PROJECT_DOMAIN } from "~/utils/constant_values";

type EntityType =
  | "buildings"
  | "citizenships"
  | "persons"
  | "places"
  | "sources"
  | "streets";

const props = defineProps<{
  id: number;
  entity_type: EntityType;
}>();

const { loggedIn } = useUserSession();
const confirm = useConfirm();
const toast = useNuxtToast();

const show_toolbar = ref(false);

// Public page slugs differ from the backend entity names for the renamed routes.
const routeSlugs: Record<EntityType, string> = {
  buildings: "katasterplan",
  citizenships: "buergermatrikel",
  persons: "personen",
  places: "orte",
  sources: "quellen",
  streets: "streets",
};

// Paths
const path = ref(`/${routeSlugs[props.entity_type]}/${props.id}`);
const edit_path = ref(`${path.value}/edit`);
const api_path = ref(`/api/${props.entity_type}/${props.id}`);

// Stores
const building_store = useBuildingStore();
const person_store = usePersonStore();
const place_store = usePlaceStore();
const source_store = useSourceStore();
const street_store = useStreetStore();
const citizenship_store = useCitizenshipStore();

// Delete handlers
const deleteHandlers: Record<string, (id: number) => Promise<void>> = {
  buildings: async (id: number) => {
    await building_store.deleteBuilding(id);
  },
  persons: async (id: number) => {
    await person_store.deletePerson(id);
  },
  places: async (id: number) => {
    await place_store.deletePlace(id);
  },
  sources: async (id: number) => {
    await source_store.deleteSource(id);
  },
  streets: async (id: number) => {
    await street_store.deleteStreet(id);
  },
  citizenships: async (id: number) => {
    await citizenship_store.deleteCitizenship(id);
  },
};

// Page actions
const actions = {
  edit_page: () => {
    toast.add({
      color: "info",
      title: "Info",
      description: "Wechle zur Editor-Ansicht",
      duration: 3000,
    });
  },
  api_view: () => {
    toast.add({
      color: "info",
      title: "Info",
      description: "Wechsle zur API-Ansicht",
      duration: 3000,
    });
  },
  copy_url: () => {
    navigator.clipboard.writeText(`${PROJECT_DOMAIN}${path.value}`);
    toast.add({
      color: "info",
      title: "Hinweis",
      description: "Link in die Zwischenablage kopiert",
      duration: 3000,
    });
  },
  delete_page: () => {
    confirm.require({
      message: "Möchten Sie diesen Eintrag wirklich löschen?",
      header: "Bestätigung erforderlich",
      icon: "pi pi-exclamation-triangle",
      rejectProps: {
        label: "Abbrechen",
        severity: "secondary",
        outlined: true,
      },
      acceptProps: {
        label: "Löschen",
      },
      accept: async () => {
        try {
          const deleteHandler = deleteHandlers[props.entity_type];
          if (!deleteHandler) return;
          await deleteHandler(props.id);
          toast.add({
            color: "info",
            title: "Bestätigung",
            description: "Löschvorgang erfolgreich",
            duration: 3000,
          });
          navigateTo(`/${routeSlugs[props.entity_type]}`);
        } catch (err) {
          toast.add({
            color: "error",
            title: "Fehler",
            description: "Löschvorgang fehlgeschlagen",
            duration: 3000,
          });
        }
      },
      reject: () => {
        toast.add({
          color: "error",
          title: "Abbruch",
          description: "Löschvorgang abgebrochen",
          duration: 3000,
        });
      },
    });
  },
};
</script>

<template>
  <div class="relative flex items-start">
    <Transition
      enter-active-class="transition-all duration-200 ease-out"
      enter-from-class="opacity-0 translate-x-2 scale-95"
      enter-to-class="opacity-100 translate-x-0 scale-100"
      leave-active-class="transition-all duration-150 ease-in"
      leave-from-class="opacity-100 translate-x-0 scale-100"
      leave-to-class="opacity-0 translate-x-2 scale-95"
    >
      <div v-if="show_toolbar" class="absolute right-full top-0 mr-2 z-50">
        <div
          class="flex flex-col gap-1 whitespace-nowrap rounded-md border border-default bg-default p-1 shadow-md"
        >
          <NuxtLink
            v-if="loggedIn"
            :to="edit_path"
            class="flex flex-row items-center space-x-2 whitespace-nowrap rounded-md p-1 text-muted hover:bg-elevated hover:text-highlighted"
            @click="actions.edit_page()"
          >
            <Icon
              name="material-symbols-edit-square-outline-sharp"
              class="text-xl"
            />
            <span class="text-sm leading-none">Bearbeiten</span>
          </NuxtLink>
          <NuxtLink
            :to="api_path"
            class="flex flex-row items-center space-x-2 whitespace-nowrap rounded-md p-1 text-muted hover:bg-elevated hover:text-highlighted"
            @click="actions.api_view()"
            target="_blank"
          >
            <Icon name="material-symbols-code" class="text-xl" />
            <span class="text-sm leading-none">API-Ansicht</span>
          </NuxtLink>
          <button
            @click="actions.copy_url()"
            class="flex flex-row items-center space-x-2 whitespace-nowrap rounded-md p-1 text-muted hover:bg-elevated hover:text-highlighted"
          >
            <Icon name="material-symbols-share-outline" class="text-xl" />
            <span class="text-sm leading-none">Teilen</span>
          </button>
          <ConfirmDialog />
          <button
            v-if="loggedIn"
            @click="actions.delete_page()"
            class="flex flex-row items-center space-x-2 whitespace-nowrap rounded-md p-1 text-error hover:bg-elevated hover:text-error"
          >
            <Icon name="material-symbols-delete-outline" class="text-xl" />
            <span class="text-sm leading-none">Eintrag löschen</span>
          </button>
        </div>
      </div>
    </Transition>
    <button
      @click="show_toolbar = !show_toolbar"
      class="cursor-pointer rounded-md p-2 leading-none hover:bg-elevated"
      title="Mehr Optionen"
    >
      <Icon name="material-symbols-more-vert" class="text-xl" />
    </button>
  </div>
</template>
