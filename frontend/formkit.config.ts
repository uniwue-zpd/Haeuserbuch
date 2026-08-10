import { defaultConfig, createInput } from '@formkit/vue'
import { rootClasses } from './formkit.theme'
import EntitySearch from "./app/components/forms/custom_inputs/EntitySearch.vue";
import TextInput from "./app/components/forms/custom_inputs/TextInput.vue";
import { genesisIcons } from "@formkit/icons";

const formkitConfig = defaultConfig({
    config: {
        rootClasses,
    },
    icons: {
        ...genesisIcons
    },
    inputs: {
        entityAutocomplete: createInput(EntitySearch, { props: ['entityType', 'isMultiple', 'optionLabel'] }),
        textInput: createInput(TextInput, { props: ['isMultiple'] })
    }
});

export default formkitConfig;
