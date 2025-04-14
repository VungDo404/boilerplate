import { Pipe, PipeTransform } from '@angular/core';
import { AclService } from "../service/acl.service";

@Pipe({
    standalone: true,
    name: 'permission'
})
export class PermissionPipe implements PipeTransform {
    constructor(private aclService: AclService) {}

    transform(id: string | number, type: ResourceType, requiredPermission: Mask): boolean {
        return this.aclService.hasPermission(id, type, requiredPermission);
    }

}
