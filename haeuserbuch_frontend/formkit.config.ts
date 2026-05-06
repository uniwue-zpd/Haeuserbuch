import { defaultConfig, createInput } from '@formkit/vue'
import { rootClasses } from './formkit.theme'
import EntitySearch from "~/components/forms/custom_inputs/EntitySearch.vue";

const formkitConfig = defaultConfig({
    config: {
        rootClasses,
    },
    inputs: {
        entityAutocomplete: createInput(EntitySearch, { props: ['entityType', 'isMultiple', 'optionLabel'] }),
    }
});

export default formkitConfig;
