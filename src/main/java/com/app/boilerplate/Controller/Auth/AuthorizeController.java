package com.app.boilerplate.Controller.Auth;

import com.app.boilerplate.Domain.Authorization.*;
import com.app.boilerplate.Service.Auth.AccessControlListService;
import com.app.boilerplate.Service.Auth.AuthorizeService;
import com.app.boilerplate.Shared.Authorization.Dto.AclDto;
import com.app.boilerplate.Shared.Authorization.Dto.CreateAclClass;
import com.app.boilerplate.Shared.Authorization.Dto.CreateAuthorityDto;
import com.app.boilerplate.Shared.Authorization.Dto.CreateSidDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "AuthorizeController")
@RequiredArgsConstructor
@RequestMapping("/authorize")
@RestController
public class AuthorizeController {
	private final AccessControlListService accessControlListService;
	private final AuthorizeService authorizeService;

	@PreAuthorize("@permissionUtil.hasPermission(#request.type.toString(), #request.objectId.toString(), " +
            "@permissionUtil.CREATE)")
	@ResponseStatus(CREATED)
	@PostMapping("/access-control-entry")
	public void addAccessControlEntry(@RequestBody @Valid AclDto request) {
		accessControlListService.addPermission(request.getType(), request.getObjectId(), request.getPermission(),
			request.getSidName(), request.getPrincipal());
	}

	@PreAuthorize("@permissionUtil.hasPermission(#request.type.toString(), #request.objectId.toString(), " +
            "@permissionUtil.WRITE)")
	@ResponseStatus(CREATED)
	@PutMapping("/access-control-entry")
	public void toggleAccessControlEntry(@RequestBody @Valid AclDto request) {
		accessControlListService.togglePermission(request.getType(), request.getObjectId(), request.getPermission(),
			request.getSidName(), request.getPrincipal());
	}

	@PreAuthorize("@permissionUtil.hasPermission(#request.type.toString(), #request.objectId.toString(), " +
            "@permissionUtil.DELETE)")
	@DeleteMapping("/access-control-entry")
	public void removeAccessControlEntry(@RequestBody @Valid AclDto request) {
		accessControlListService.removePermission(request.getType(), request.getObjectId(), request.getPermission(),
			request.getSidName(), request.getPrincipal());
	}

	@PreAuthorize("@permissionUtil.hasPermission(@permissionUtil.AUTHORIZATION, @permissionUtil.READ)")
	@GetMapping("/access-control-entry/user/{id}")
	public Page<AclEntry> getEntries(@ParameterObject Pageable pageable, @PathVariable UUID id) {
		return authorizeService.getEntries(pageable, id);
	}

	@PreAuthorize("@permissionUtil.hasPermission(@permissionUtil.AUTHORIZATION, @permissionUtil.CREATE)")
	@ResponseStatus(CREATED)
	@PostMapping("/class")
	public Long addClass(@RequestBody @Valid CreateAclClass request) {
		return accessControlListService.callCreateOrRetrieveClass(request.getType(), request.getAllowCreate(),
                request.getIdType());
	}

	@PreAuthorize("@permissionUtil.hasPermission(@permissionUtil.AUTHORIZATION, @permissionUtil.READ)")
	@GetMapping("/class")
	public Page<AclClass> getClasses(@ParameterObject Pageable pageable) {
		return authorizeService.getClasses(pageable);
	}

	@PreAuthorize("@permissionUtil.hasPermission(@permissionUtil.AUTHORIZATION, @permissionUtil.GRANT)")
	@GetMapping("/authority/user/{id}")
	public Collection<GrantedAuthority> getGrantedAuthorities(@PathVariable String id) {
		return authorizeService.getGrantedAuthorities(id);
	}

	@PreAuthorize("@permissionUtil.hasPermission(@permissionUtil., @permissionUtil.)")
	@ResponseStatus(CREATED)
	@PostMapping("/authority")
	public Authority grantAuthority(@RequestBody @Valid CreateAuthorityDto request) {
		return authorizeService.grantedAuthority(request);
	}

	@PreAuthorize("@permissionUtil.hasPermission(@permissionUtil.AUTHORIZATION, @permissionUtil.READ)")
	@GetMapping("/object-identity")
	public Page<AclObjectIdentity> getObjects(@ParameterObject Pageable pageable) {
		return authorizeService.getObjects(pageable);
	}

	@PreAuthorize("@permissionUtil.hasPermission(@permissionUtil.AUTHORIZATION, @permissionUtil.CREATE)")
	@ResponseStatus(CREATED)
	@PostMapping("/sid")
	public Long addSid(@RequestBody @Valid CreateSidDto request) {
		return accessControlListService.callCreateOrRetrieveSidPrimaryKey(request.getSidName(),
                request.getSidIsPrincipal(), request.getAllowCreate());
	}

	@PreAuthorize("@permissionUtil.hasPermission(@permissionUtil.AUTHORIZATION, @permissionUtil.READ)")
	@GetMapping("/sid")
	public Page<AclSid> getSid(@ParameterObject Pageable pageable) {
		return authorizeService.getSid(pageable);
	}
}
