export const DEFAULT_MAP_CENTER: [number, number] = [9.9358911, 49.7929984];
export const PROJECT_DOMAIN = '';

// Must match spring.servlet.multipart.max-file-size in the backend
export const MAX_UPLOAD_FILE_SIZE = 30 * 1024 * 1024;
export const ALLOWED_UPLOAD_TYPES = ["image/png", "image/jpeg", "application/pdf"];
export const UPLOAD_ACCEPT = [".png", ".jpg", ".jpeg", ...ALLOWED_UPLOAD_TYPES].join(",");
