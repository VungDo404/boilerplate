import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class MenuService {
    defaultMenuState: MenuKey[] = ['main'];

    private isVisibleSubject = new BehaviorSubject<boolean>(false);
    private menuTrackingSubject = new BehaviorSubject<MenuKey[]>(this.defaultMenuState);

    constructor() {}

    get menuTracking() {
        return this.menuTrackingSubject.value;
    }

    get isVisible() {
        return this.isVisibleSubject.value;
    }

    navigateToSubmenu(key: MenuKey) {
        const current = this.menuTracking;
        if (current.at(-1) !== key) {
            this.menuTrackingSubject.next([...current, key])
        }
    }

    navigateBack() {
        const current = this.menuTracking;
        if (current.length > 1) {
            current.pop();
            this.menuTrackingSubject.next([...current]);
        }
    }

    hide() {
        this.isVisibleSubject.next(false);
        this.resetMenuState();
    }

    show() {
        this.isVisibleSubject.next(true);
    }

    resetMenuState() {
        this.menuTrackingSubject.next(this.defaultMenuState);
    }


}
