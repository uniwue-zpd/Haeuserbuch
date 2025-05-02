import { defaultConfig } from '@formkit/vue'
import { rootClasses } from './formkit.theme'

const formkitConfig = defaultConfig({
    config: {
        rootClasses,
    },
});

export default formkitConfig;
