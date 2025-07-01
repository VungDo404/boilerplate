
declare interface LinkItem{
    type: 'link';
    label: string;
    value?: string;
    routerLink?: string;
    leftIcon?: string;
    rightIcon?: string;
    cb?: (item: LinkItem) => void;
    target?: keyof MenuData;
    authority: BaseAuthority;
}

declare interface ProfileItem{
    type: 'profile';
    displayName: string | null;
    username: string | null;
    avatar: string;
    authority: BaseAuthority;
}

declare interface SectionItem<T>{
    id?: string;
    items: T[]
}

declare interface MenuSection<T>{
    title?: string;
    sections: SectionItem<T>[];
}

declare interface MenuData {
    main: MenuSection<LinkItem | ProfileItem>;
    language: MenuSection<LinkItem>;
}

declare type MenuKey = keyof MenuData;

declare interface SettingSidebarItem {
    icon?: string;
    label: string;
    routerLink: string;
    isRender: boolean;
}

declare type AccordionMode = 'compact' | 'wide';
