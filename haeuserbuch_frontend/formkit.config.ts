import { defaultConfig, createInput } from '@formkit/vue'
import { rootClasses } from './formkit.theme'
import PersonSearch from "~/components/forms/custom_inputs/PersonSearch.vue";
import SourceSearch from "~/components/forms/custom_inputs/SourceSearch.vue";
import BuildingSearch from "~/components/forms/custom_inputs/BuildingSearch.vue";
import JobSearch from "~/components/forms/custom_inputs/JobSearch.vue";
import PlaceSearch from "~/components/forms/custom_inputs/PlaceSearch.vue";
import ReligionSearch from "~/components/forms/custom_inputs/ReligionSearch.vue";
import WeaponSearch from "~/components/forms/custom_inputs/WeaponSearch.vue";

const formkitConfig = defaultConfig({
    config: {
        rootClasses,
    },
    inputs: {
        personAutocomplete: createInput(PersonSearch, { props: ['isMultiple'] }),
        sourceAutocomplete: createInput(SourceSearch, { props: ['isMultiple'] }),
        buildingAutocomplete: createInput(BuildingSearch),
        jobAutocomplete: createInput(JobSearch),
        placeAutocomplete: createInput(PlaceSearch, { props: ['isMultiple'] }),
        religionAutocomplete: createInput(ReligionSearch),
        weaponAutocomplete: createInput(WeaponSearch)
    }
});

export default formkitConfig;
