import { Directive, ElementRef, EventEmitter, Input, OnDestroy, Output } from '@angular/core';

@Directive({
    standalone: true,
    selector: '[appMenu]',
    exportAs: 'appMenu'
})
export class MenuDirective implements OnDestroy {
    @Input() ignoreClickSelectors: string[] = [];
    @Input() spacing = 8;
    @Input() position: MenuPosition = 'left';
    @Output() onPositionChange = new EventEmitter<{ top: number; left: number }>();
    @Output() onHide = new EventEmitter<void>();

    private triggerElement?: HTMLElement;
    private resizeListener = () => this.updatePosition();
    private clickListener = (event: Event) => this.handleClickOutside(event);

    constructor(private el: ElementRef<HTMLElement>) {}

    show(trigger: HTMLElement) {
        this.triggerElement = trigger;
        setTimeout(() => this.updatePosition(), 0);
        setTimeout(() => this.addClickOutsideListener(), 10);
        this.addResizeListener();
    }

    hide() {
        this.removeClickOutsideListener();
        this.removeResizeListener();
        this.triggerElement = undefined;
        this.onHide.emit();
    }

    private updatePosition() {
        if (!this.triggerElement) return;

        const rect = this.triggerElement.getBoundingClientRect();
        const menu = this.el.nativeElement;
        const menuRect = menu.getBoundingClientRect();
        let position: { top: number; left: number };
        switch (this.position) {
            case 'left':
                position = {
                    top: rect.top + window.scrollY,
                    left: rect.left + window.scrollX - menuRect.width - this.spacing,
                };
                break;

            case 'bottom-left':
                position = {
                    top: rect.bottom + window.scrollY + this.spacing,
                    left: rect.left + window.scrollX - menuRect.width + 40,
                };
                break;
            default:
                position = {
                    top: rect.top + window.scrollY,
                    left: rect.left + window.scrollX - menuRect.width - this.spacing,
                };
        }

        this.onPositionChange.emit(position);
    }

    private addResizeListener() {
        window.addEventListener('resize', this.resizeListener);
    }

    private removeResizeListener() {
        window.removeEventListener('resize', this.resizeListener);
    }

    private addClickOutsideListener() {
        document.addEventListener('click', this.clickListener, true);
    }

    private removeClickOutsideListener() {
        document.removeEventListener('click', this.clickListener, true);
    }

    private handleClickOutside(event: Event) {
        const target = event.target as HTMLElement;

        if (
            this.el.nativeElement.contains(target) ||
            (this.triggerElement && this.triggerElement.contains(target))
        ) {
            return;
        }

        if (this.ignoreClickSelectors.some(selector => target.closest(selector))) {
            return;
        }

        this.hide();
    }

    ngOnDestroy(): void {
        this.hide();
    }

}
