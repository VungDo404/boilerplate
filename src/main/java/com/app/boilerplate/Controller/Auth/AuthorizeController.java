package com.app.boilerplate.Controller.Auth;

import com.app.boilerplate.Decorator.RateLimit.RateLimit;
import com.app.boilerplate.Domain.Authorization.*;
import com.app.boilerplate.Service.Authorization.AccessControlListService;
import com.app.boilerplate.Service.Authorization.AuthorizeService;
import com.app.boilerplate.Shared.Authorization.Dto.*;
import com.app.boilerplate.Util.PermissionUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RateLimit(capacity = 100, tokens = 10, duration = 5, timeUnit = ChronoUnit.SECONDS, key = "'authorize-' + #ip")
@Tag(name = "AuthorizeController")
@RequiredArgsConstructor
@RequestMapping("/authorize")
@RestController
public class AuthorizeController {
	private final AccessControlListService accessControlListService;
	private final AuthorizeService authorizeService;


	@PreAuthorize("hasPermission(#request.objectId.toString(), #request.type.toString(), '" + PermissionUtil.CREATE +
		"')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/access-control-entry")
	public void addAccessControlEntry(@RequestBody @Valid AclDto request) {
		accessControlListService.addPermission(
			request.getType(),
			request.getObjectId(),
			request.getPermission(),
			request.getSidName(),
			request.getPrincipal()
		);
	}


	@PreAuthorize("hasPermission(#request.objectId.toString(), #request.type.toString(), '" + PermissionUtil.WRITE +
		"')")
	@ResponseStatus(HttpStatus.CREATED)
	@PutMapping("/access-control-entry")
	public void toggleAccessControlEntry(@RequestBody @Valid AclDto request) {
		accessControlListService.togglePermission(
			request.getType(),
			request.getObjectId(),
			request.getPermission(),
			request.getSidName(),
			request.getPrincipal()
		);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.READ + "')")
	@GetMapping("/access-control-entry")
	public Page<AclEntry> getEntries(@ParameterObject Pageable pageable) {
		return authorizeService.getEntries(pageable);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.READ + "')")
	@GetMapping("/access-control-entry/sid/{id}")
	public Page<AclEntry> getEntriesBySid(@ParameterObject Pageable pageable, @PathVariable Long id) {
		return authorizeService.getEntries(pageable, id);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.READ + "')")
	@GetMapping("/access-control-entry/user/{id}")
	public Page<AclEntry> getEntriesByUserId(@ParameterObject Pageable pageable, @PathVariable UUID id) {
		return authorizeService.getEntries(pageable, id);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.CREATE + "')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/class")
	public Long addClass(@RequestBody @Valid CreateAclClass request) {
		return accessControlListService.callCreateOrRetrieveClass(
			request.getType(),
			request.getAllowCreate(),
			request.getIdType()
		);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.READ + "')")
	@GetMapping("/class")
	public Page<AclClass> getClasses(@ParameterObject Pageable pageable) {
		return authorizeService.getClasses(pageable);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.APPLICATION + "', '" + PermissionUtil.ADMIN + "')")
	@GetMapping("/authority/user/{id}")
	public Collection<GrantedAuthority> getGrantedAuthorities(@PathVariable String id) {
		return authorizeService.getSidGrantedAuthorities(id);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.APPLICATION + "', '" + PermissionUtil.ADMIN + "')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/authority")
	public Authority grantAuthority(@RequestBody @Valid CreateAuthorityDto request) {
		return authorizeService.grantedAuthority(request);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.READ + "')")
	@GetMapping("/object-identity")
	public Page<AclObjectIdentity> getObjects(@ParameterObject Pageable pageable) {
		return authorizeService.getObjects(pageable);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.CREATE + "')")
	@PostMapping("/object-identity")
	public Long addObjects(@ParameterObject CreateObjectIdentityDto request) {
		return authorizeService.addObjectIdentity(request);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.CREATE + "')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/sid")
	public Long addSid(@RequestBody @Valid CreateSidDto request) {
		return accessControlListService.callCreateOrRetrieveSidPrimaryKey(
			request.getSidName(),
			request.getSidIsPrincipal(),
			request.getAllowCreate()
		);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.READ + "')")
	@GetMapping(value = "/sid", params = "!principal")
	public Page<AclSid> getSid(@ParameterObject Pageable pageable) {
		return authorizeService.getSid(pageable);
	}

	@PreAuthorize("hasPermission(" + PermissionUtil.ROOT + ", '" + PermissionUtil.AUTHORIZATION + "', '" + PermissionUtil.READ + "')")
	@GetMapping(value = "/sid", params = "principal")
	public List<AclSid> getSidByPrincipal(@RequestParam(defaultValue = "true") boolean principal) {
		return authorizeService.getSidByPrincipal(principal);
	}
}
