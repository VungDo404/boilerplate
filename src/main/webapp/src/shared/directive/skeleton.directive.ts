import {
  ComponentRef,
  Directive,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  ViewContainerRef
} from '@angular/core';
import { Skeleton } from "primeng/skeleton";

@Directive({
  standalone: true,
  selector: '[appSkeleton]'
})
export class SkeletonDirective implements OnChanges, OnDestroy {
  @Input() busyIf!: boolean;
  @Input() shape: string = 'rectangle';
  @Input() width!: string;
  @Input() height!: string;

  private readonly originalDisplay: string;
  private componentRef: ComponentRef<Skeleton> | null = null;
  private readonly hostElement: HTMLElement;

  constructor(private viewContainerRef: ViewContainerRef, private elementRef: ElementRef) {
    this.hostElement = this.elementRef.nativeElement;
    this.originalDisplay = this.hostElement.style.display;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['busyIf']) {
      if (this.busyIf) {
        this.showSkeleton();
      } else {
        this.showOriginalContent();
      }
    }
  }

  private showSkeleton() {
    if (!this.componentRef) {
      this.componentRef = this.viewContainerRef.createComponent(Skeleton);
      const component = this.componentRef.instance;
      const skeletonEl = this.componentRef.location.nativeElement;
      component.shape = this.shape;

      const { width, height } = this.hostElement.getBoundingClientRect();
      skeletonEl.style.width = this.width ? this.width : `${width}px`;
      skeletonEl.style.height = this.height ? this.height : `${height}px`;
      component.width = "100%";
      component.height = "100%";
      this.hostElement.style.display = 'none';

      const skeletonElement = this.componentRef.location.nativeElement;
      this.hostElement.parentNode?.insertBefore(skeletonElement, this.hostElement);
    }
  }

  private showOriginalContent() {
    if (this.componentRef) {
      this.componentRef.destroy();
      this.componentRef = null;
      this.hostElement.style.display = this.originalDisplay;
    }
  }

  ngOnDestroy() {
    if (this.componentRef) {
      this.componentRef.destroy();
    }
  }
}
