<script setup lang="ts">
import UniversalSkeleton from "~/components/UI/skeletons/UniversalSkeleton.vue";
import FetchError from "~/components/UI/FetchError.vue";
import type { CitizenshipPreviewDTO, PersonPreviewDTO } from "~/utils/types";

/**
 * Paged, filterable list of places of the place register. Each place can be expanded to show its related
 * persons (`type="persons"`) or citizenship entries (`type="citizenships"`).
 */
const props = defineProps<{
  type: "persons" | "citizenships";
}>();

const placeApi = usePlaces();

const page = ref(0);
const rowsPerPage = ref(10);
const rowsPerPageOptions = [10, 25, 50];
const nameFilter = ref("");
const debouncedName = ref("");

const updateDebouncedName = debounce((value: string) => {
  debouncedName.value = value.trim();
}, 400);
watch(nameFilter, (value) => updateDebouncedName(value));
watch([debouncedName, rowsPerPage], () => {
  page.value = 0;
});

const { data: register, pending: isLoading, error: hasError } = await useAsyncData(
  `place-register-${props.type}`,
  () => placeApi.getPlaceRegister<PersonPreviewDTO | CitizenshipPreviewDTO>({
    type: props.type,
    name: debouncedName.value || undefined,
    page: page.value,
    size: rowsPerPage.value,
  }),
  { watch: [page, rowsPerPage, debouncedName] },
);

const places = computed(() => register.value?.content ?? []);
const totalPlaces = computed(() => register.value?.totalElements ?? 0);

function onPageChange(nextPage: number) {
  page.value = nextPage - 1;
}
</script>

<template>
  <FetchError v-if="hasError" :error="hasError" />
  <div v-else class="flex flex-col gap-4">
    <UInput
        v-model="nameFilter"
        icon="i-lucide-search"
        placeholder="Nach Ortsnamen filtern"
        class="w-full sm:max-w-sm"
    />
    <UniversalSkeleton v-if="isLoading && !register" />
    <ul
      v-else
      class="divide-y divide-muted rounded-md border border-default bg-default shadow-md transition-opacity"
      :class="{ 'opacity-60': isLoading }"
    >
      <li v-for="place in places" :key="place.id" class="flex items-start gap-1 p-2">
        <UCollapsible class="min-w-0 flex-1">
          <UButton
            icon="i-lucide-chevron-right"
            color="neutral"
            variant="ghost"
            block
            class="group justify-start"
            :ui="{ leadingIcon: 'transition-transform duration-200 group-data-[state=open]:rotate-90' }"
          >
            <span class="truncate font-medium">{{ place.realName || `Ort mit ID ${place.id}` }}</span>
            <UBadge :label="place.entries.length" color="neutral" variant="subtle" size="sm" class="ml-auto" />
          </UButton>
          <template #content>
            <div class="px-3 pb-2 pt-2">
              <UIEntityList
                :type="type"
                :title="type === 'persons' ? 'Herkunfts- oder Wegzugsort von' : 'Erwähnt in den Einträgen'"
                :entries="place.entries as PersonPreviewDTO[] | CitizenshipPreviewDTO[]"
              />
            </div>
          </template>
        </UCollapsible>
        <UTooltip text="Zur Ortsseite">
          <UButton
            :to="`/orte/${place.id}`"
            icon="i-lucide-arrow-up-right"
            color="neutral"
            variant="ghost"
            :aria-label="`Ortsseite von ${place.realName ?? 'Ort'} öffnen`"
          />
        </UTooltip>
      </li>
      <li v-if="places.length === 0" class="px-4 py-6 text-center text-muted">
        Keine Orte gefunden
      </li>
    </ul>
    <!-- Navigation controls -->
    <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <USelect
        v-model="rowsPerPage"
        :items="rowsPerPageOptions"
        class="w-24"
      />
      <UPagination
        :page="page + 1"
        :items-per-page="rowsPerPage"
        :total="totalPlaces"
        @update:page="onPageChange"
      />
    </div>
  </div>
</template>
