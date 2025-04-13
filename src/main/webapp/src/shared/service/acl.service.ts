import { Injectable } from '@angular/core';
import { SessionService } from "./session.service";
import { ROOT_OBJECT } from "../const/app.const";
import { Action } from "../const/app.enum";

@Injectable({
    providedIn: 'root'
})
export class AclService {
    private hierarchy: ObjectHierarchy[] = [];

    constructor(private sessionService: SessionService) {
        this.initialize();
    }

    hasPermissionOnType(type: ResourceType, requiredPermission: Mask): boolean {
        const authority = this.sessionService.permissions.find(permission =>
             permission.type === type && this.isGranted(permission, requiredPermission) && permission.granting
        )
        if(authority) return true;

        const currentObject = this.getObjectHierarchy(ROOT_OBJECT, type);
        const parentObject = currentObject.parent;
        if(!parentObject) return false;

        return this.hasPermissionOnType(parentObject.type, requiredPermission);
    }

    hasPermission(id: string | number, type: ResourceType, requiredPermission: Mask): boolean {
        const authority = this.sessionService.permissions.find(permission =>
            permission.id.toString() === id.toString() && permission.type === type && this.isGranted(permission, requiredPermission)
        )
        if(authority) return authority.granting;

        const currentObject = this.getObjectHierarchy(id, type);
        const parentObject = currentObject.parent;
        if(!parentObject) return false;

        return this.hasPermission(parentObject.id, parentObject.type, requiredPermission);
    }

    private initialize() {
        const application = this.createObjectHierarchy(ROOT_OBJECT, "Application");
        const authentication = this.createObjectHierarchy(ROOT_OBJECT, "Authentication", application);
        const authorization = this.createObjectHierarchy(ROOT_OBJECT, "Authorization", application);
        const user = this.createObjectHierarchy(ROOT_OBJECT, "User", application);
        const file = this.createObjectHierarchy(ROOT_OBJECT, "File", application);
        const domain = this.createObjectHierarchy(ROOT_OBJECT, "Domain", application);

        this.hierarchy.push(application, authentication, authorization, user, file, domain);
    }

    private getObjectHierarchy(id: string | number, type: ResourceType): ObjectHierarchy {
        const parent = this.hierarchy.find(obj => obj.id === ROOT_OBJECT && obj.type === type);
        if (id !== ROOT_OBJECT) {
            return this.createObjectHierarchy(id, type, parent);
        }
        if (parent) return parent;
        return this.createObjectHierarchy(id, type);
    }

    private createObjectHierarchy(id: string | number, type: ResourceType, parent?: ObjectHierarchy): ObjectHierarchy {
        return {
            id: id,
            type: type,
            parent: parent ?? null
        }
    }

    private isGranted(a: Authority, rp: Mask){
        return a.mask === rp || a.mask === Action.Admin
    }
}
