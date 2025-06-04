export enum Action{
    Read = 1 << 0,
    Write = 1 << 1,
    Create = 1 << 2,
    Delete = 1 << 3,
    Grant = 1 << 4,
    Admin = 1 << 5
}

export enum Locale {
    English = 'en',
    Vietnamese = 'vi'
}