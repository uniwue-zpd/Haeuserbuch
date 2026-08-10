export function debounce(fn: (...args: any[]) => void, wait: number) {
    let timer: ReturnType<typeof setTimeout>

    return (...args: any[]) => {
        clearTimeout(timer)
        timer = setTimeout(() => fn(...args), wait)
    }
}
