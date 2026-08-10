<script setup lang="ts">
import { computed } from "vue";
import { isBuildingFeature, type BuildingFeature } from "~/utils/GeoJsonTypes";
import type { PersonPreviewDTO } from "~/utils/types";
import BuildingSkeleton from "~/components/UI/skeletons/BuildingSkeleton.vue";
import FetchError from "~/components/UI/FetchError.vue";

const route = useRoute();
definePageMeta({
  layout: "building",
  key: (currentRoute) => currentRoute.fullPath,
});

const buildingId = Number(route.params.id);
const buildingStore = useBuildingStore();

const {
  data: buildingItem,
  error: hasError,
  pending: isLoading,
} = await useAsyncData(`building-${buildingId}`, () =>
  buildingStore.getBuilding(buildingId),
);
const {
  data: associatedPeople,
  error: associatedPeopleError,
  pending: associatedPeoplePending,
  refresh: refreshAssociatedPeople,
} = await useAsyncData(
  `associated-people-building-${buildingId}`,
  () =>
    $fetch<PersonPreviewDTO[]>("/api/persons/filter", {
      query: { "associated-building-id": buildingId },
    }),
  { default: () => [] },
);

const building = computed<BuildingFeature | null>(() => {
  const feature = buildingItem.value;
  return feature && isBuildingFeature(feature) ? feature : null;
});

useHead(() => ({
  title: building.value?.properties.districtPropertyNumber
    ? `${building.value.properties.districtPropertyNumber} - Gebäudeverzeichnis`
    : "Gebäude - Gebäudeverzeichnis",
}));
</script>

<template>
  <BuildingSkeleton v-if="isLoading" />
  <FetchError v-else-if="hasError" :error="hasError" />
  <BuildingsDetails
    v-else-if="building"
    :building="building"
    :associated-people="associatedPeople"
    :associated-people-pending="associatedPeoplePending"
    :associated-people-error="Boolean(associatedPeopleError)"
    compact
    bento
    @retry-relationships="refreshAssociatedPeople"
  />
</template>
