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

/**
 * Formats a file size in bytes as a human-readable string.
 * @param bytes file size in bytes
 */
export function formatFileSize(bytes?: number | null): string {
    if (!bytes || bytes < 0) return 'Unbekannte Dateigröße';
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}
