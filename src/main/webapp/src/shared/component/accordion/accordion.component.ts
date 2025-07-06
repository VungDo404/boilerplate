import { AfterViewInit, Component, ContentChildren, Input, QueryList, TemplateRef } from '@angular/core';
import { NgIf } from "@angular/common";
import { AccordionItemComponent } from "./accordion-item/accordion-item.component";
import { TranslatePipe } from "@ngx-translate/core";


export interface AccordionItem {
    title: string;
    titleIcon: string;
    description: string;
    descriptionIcon?: string;
    content?: string | TemplateRef<any>;
    routerLink?: string;
}

@Component({
    selector: 'app-accordion',
    imports: [
        NgIf,
        TranslatePipe
    ],
    templateUrl: './accordion.component.html',
    standalone: true,
    styleUrl: './accordion.component.scss'
})
export class AccordionComponent implements AfterViewInit {
    @ContentChildren(AccordionItemComponent) accordionItems!: QueryList<AccordionItemComponent>;
    @Input() title!: string | undefined;
    @Input() description!: string | undefined;
    private openItemId: number | null = null;

    ngAfterViewInit(): void {
        this.accordionItems.changes.subscribe((children: AccordionItemComponent[]) => {
            children.forEach((child) => {
                child.onToggleItem.subscribe(clickedItem => {
                    this.handleItemClick(clickedItem);
                })
            })
        })
    }

    private handleItemClick(clickedItem: AccordionItemComponent) {
        if (this.openItemId === clickedItem.id) {
            this.openItemId = null;
        } else {
            if (this.openItemId !== null) {
                const previouslyOpenItem = this.accordionItems.find(item => item.id === this.openItemId);
                if (previouslyOpenItem) {
                    previouslyOpenItem.isOpen = false;
                }
            }
            clickedItem.isOpen = true;
            this.openItemId = clickedItem.id;
        }
    }
}
