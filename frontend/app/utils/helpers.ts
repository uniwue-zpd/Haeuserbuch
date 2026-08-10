/**
 * Shortens a title to a specified number of words, adding ellipsis if truncated.
 * @param title text to shorten
 * @param whitespace number of words to keep before truncating
 */
export function title_shortener(title: string, whitespace: number = 3): string {
    const words = title.split(' ');
    if (words.length <= whitespace) return title;
    return words.slice(0, whitespace).join(' ') + '...';
}
