<script setup lang="ts">
const props = defineProps({
  context: Object,
});

const isMultiple: boolean = props.context?.isMultiple;

const value = computed({
  get: () => props.context?._value ?? (isMultiple ? [] : null),
  set: (val) => props.context?.node.input(val)
})
</script>

<template>
  <AutoComplete
      v-model="value"
      placeholder="Bei Listen die Eingabetaste drücken"
      :typeahead="false"
      :multiple="isMultiple"
      @clear="props.context?.node.input(isMultiple ? [] : null)"
      fluid
      showClear
      class="min-w-full"
  />
</template>
