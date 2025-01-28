package com.app.boilerplate.Shared.Authorization.Model;

import com.app.boilerplate.Domain.Authorization.AclClass;
import com.app.boilerplate.Domain.Authorization.AclObjectIdentity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AclObjectIdentityWithLevelModel {
	private Long id;
	private AclClass objectIdClass;
	private String objectIdIdentity;
	private AclObjectIdentity parentObject;
	private int level;
}
