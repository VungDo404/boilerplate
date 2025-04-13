import { Directive, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { AclService } from "../service/acl.service";

@Directive({
    standalone: true,
    selector: '[permission]'
})
export class PermissionDirective implements OnInit {
    @Input('action') mask!: Action;
    @Input('type') type!: ResourceType;
    @Input('id') id!: string | number;

    constructor(
        private aclService: AclService,
        private templateRef: TemplateRef<any>,
        private viewContainer: ViewContainerRef
    ) {}

    ngOnInit(): void {
        if (!this.mask || !this.type || !this.id) {
            return;
        }
        if(this.aclService.hasPermission(this.id, this.type, this.mask)){
            this.viewContainer.createEmbeddedView(this.templateRef);
        }else{
            this.viewContainer.clear();
        }
    }

}
