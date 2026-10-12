<script setup lang="ts">
const props = defineProps({
  context: Object,
});

const loading = ref(false);

type EntityTypes =
    'building'
    | 'district'
    | 'job'
    | 'person'
    | 'place'
    | 'quarter'
    | 'religion'
    | 'source'
    | 'street'
    | 'weapon'
    | 'file';

const entityType: EntityTypes = props.context?.entityType;
const isMultiple: boolean = props.context?.isMultiple;
const optionLabel: string = props.context?.optionLabel;

const suggestions = ref<any[]>([]);

const buildingStore = useBuildingStore();
const districtStore = useDistrictStore();
const jobStore = useJobStore();
const personStore = usePersonStore();
const placeStore = usePlaceStore();
const quarterStore = useQuarterStore();
const religionStore = useReligionStore();
const sourceStore = useSourceStore();
const streetStore = useStreetStore();
const weaponStore = useWeaponStore();
const fileApi = useFiles();

const isImageFile = (file: { type?: string }) => {
  return !!file.type?.startsWith("image/");
};

const getFileIcon = (file: { type?: string }) => {
  if (file.type === "application/pdf") return "i-lucide-file-text";
  return "i-lucide-file";
};

const debouncedSearch = debounce(async (query: string) => {
  loading.value = true;
  switch (entityType) {
    case 'building':
      suggestions.value = await buildingStore.searchBuildings(query);
      break;
    case 'district':
      suggestions.value = await districtStore.searchDistricts(query);
      break;
    case 'job':
      suggestions.value = await jobStore.searchJobs(query);
      break;
    case 'person':
      suggestions.value = await personStore.searchPeople(query);
      break;
    case 'place':
      suggestions.value = await placeStore.searchPlaces(query);
      break;
    case 'quarter':
      suggestions.value = await quarterStore.searchQuarters(query);
      break;
    case 'religion':
      suggestions.value = await religionStore.searchReligions(query);
      break;
    case 'source':
      suggestions.value = await sourceStore.searchSources(query);
      break;
    case 'street':
      suggestions.value = await streetStore.searchStreets(query);
      break;
    case 'weapon':
      suggestions.value = await weaponStore.searchWeapons(query);
      break;
    case 'file':
      const files = await fileApi.searchFiles(query);
      suggestions.value = files.map(file => ({
        id: file.id,
        originalName: file.originalName,
        type: file.type,
      }));
      break;
  }
  loading.value = false;
}, 300);

const onComplete = (event: any) => {
  debouncedSearch(event.query)
}

const value = computed({
  get: () => props.context?.value ?? null,
  set: (val) => props.context?.node.input(val),
});
</script>

<template>
  <div>
    <AutoComplete
        v-model="value"
        :suggestions="suggestions"
        :loading="loading"
        @complete="onComplete"
        @clear="isMultiple ? props.context?.node.input([]) : props.context?.node.input(null)"
        :optionLabel="optionLabel"
        class="min-w-full"
        :dropdown="!isMultiple"
        showClear
        :multiple="isMultiple"
    >
      <template #option="{ option }">
        <div
            v-if="entityType === 'file'"
            class="flex items-center gap-3"
        >
          <img
              v-if="isImageFile(option)"
              :src="fileApi.getFileContentUrl(option.id)"
              class="h-10 w-10 shrink-0 rounded-md border object-cover"
              alt="Preview"
          />
          <div
              v-else
              class="flex h-10 w-10 shrink-0 items-center justify-center rounded-md border bg-elevated"
          >
            <UIcon
                :name="getFileIcon(option)"
                class="size-5 text-muted"
            />
          </div>
          <span class="truncate">{{ option.originalName }}</span>
        </div>
        <span v-else>{{ option[optionLabel] }}</span>
      </template>
    </AutoComplete>
  </div>
</template>
