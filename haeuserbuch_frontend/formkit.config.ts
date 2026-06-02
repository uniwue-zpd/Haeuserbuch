import { defaultConfig, createInput } from '@formkit/vue'
import { rootClasses } from './formkit.theme'
import EntitySearch from "~/components/forms/custom_inputs/EntitySearch.vue";
import TextInput from "~/components/forms/custom_inputs/TextInput.vue";

const formkitConfig = defaultConfig({
    config: {
        rootClasses,
    },
    inputs: {
        entityAutocomplete: createInput(EntitySearch, { props: ['entityType', 'isMultiple', 'optionLabel'] }),
        textInput: createInput(TextInput, { props: ['isMultiple'] })
    }
});

export default formkitConfig;
