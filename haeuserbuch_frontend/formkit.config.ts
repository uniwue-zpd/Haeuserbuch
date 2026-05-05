import { defaultConfig, createInput } from '@formkit/vue'
import { rootClasses } from './formkit.theme'
import PersonSearch from "~/components/forms/custom_inputs/PersonSearch.vue";
import SourceSearch from "~/components/forms/custom_inputs/SourceSearch.vue";

const formkitConfig = defaultConfig({
    config: {
        rootClasses,
    },
    inputs: {
        personAutocomplete: createInput(PersonSearch, { props: ['isMultiple'] }),
        sourceAutocomplete: createInput(SourceSearch)
    }
});

export default formkitConfig;
