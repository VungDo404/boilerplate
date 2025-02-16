package com.app.boilerplate.Util;

import com.app.boilerplate.Domain.Common.AuditEnversRevision;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ApplicationRevisionListener implements EntityTrackingRevisionListener {
	private final SecurityUtil securityUtil;
	private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	@Override
	public void newRevision(final Object revisionEntity) {
		final var revision = (AuditEnversRevision) revisionEntity;
		final var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getPrincipal())){
			revision.setUserId(securityUtil.getUserId());
		}else{
			revision.setUserId(SYSTEM_USER_ID);
		}

	}

	@Override
	public void entityChanged(Class entityClass, String entityName,
							  Object entityId, RevisionType revisionType,
							  Object revisionEntity) {
		final var revision = (AuditEnversRevision) revisionEntity;
	}
}
