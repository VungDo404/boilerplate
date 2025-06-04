declare interface LinkItem{
    type: 'link';
    label: string;
    value?: string;
    routerLink?: string;
    leftIcon?: string;
    rightIcon?: string;
    cb?: (item: LinkItem) => void;
    target?: keyof MenuData;
}

declare interface ProfileItem{
    type: 'profile';
    displayName: string;
    username: string;
    avatar: string;
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

