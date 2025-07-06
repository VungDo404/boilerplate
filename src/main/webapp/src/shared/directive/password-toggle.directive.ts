import { AfterViewInit, Directive, ElementRef, Renderer2 } from '@angular/core';

@Directive({
    standalone: true,
    selector: '[passwordToggle]'
})
export class PasswordToggleDirective implements AfterViewInit {
    private show = false;
    private toggleIcon!: HTMLElement;
    constructor(private el: ElementRef, private renderer: Renderer2) { }

    ngAfterViewInit(): void {
        const parent = this.renderer.parentNode(this.el.nativeElement);
        this.toggleIcon = this.renderer.createElement('i');

        this.renderer.addClass(this.toggleIcon, 'pi');
        this.renderer.addClass(this.toggleIcon, 'pi-eye-slash');
        this.renderer.addClass(this.toggleIcon, 'password-toggle-icon');

        this.renderer.setStyle(this.toggleIcon, 'position', 'absolute');
        this.renderer.setStyle(this.toggleIcon, 'right', '1rem');
        this.renderer.setStyle(this.toggleIcon, 'top', '50%');
        this.renderer.setStyle(this.toggleIcon, 'cursor', 'pointer');

        this.renderer.appendChild(parent, this.toggleIcon);

        this.renderer.listen(this.toggleIcon, 'click', () => {
            this.togglePassword();
        });
    }

    private togglePassword(): void {
        this.show = !this.show;
        const input = this.el.nativeElement as HTMLInputElement;

        input.type = this.show ? 'text' : 'password';

        this.renderer.removeClass(this.toggleIcon, this.show ? 'pi-eye-slash' : 'pi-eye');
        this.renderer.addClass(this.toggleIcon, this.show ? 'pi-eye' : 'pi-eye-slash');
    }

}
