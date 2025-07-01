package com.app.boilerplate.Util;

import com.app.boilerplate.Domain.Common.AuditEnversRevision;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApplicationRevisionListener implements EntityTrackingRevisionListener {
	@Override
	public void newRevision(final Object revisionEntity) {
		final var revision = (AuditEnversRevision) revisionEntity;
		final var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !AppConsts.ANONYMOUS_USER.equals(authentication.getPrincipal())){
			revision.setUserId(SecurityUtil.getAccessJwt().getSub());
		}else{
			revision.setUserId(AppConsts.SYSTEM_USER_ID);
		}
	}

	@Override
	public void entityChanged(Class entityClass, String entityName,
							  Object entityId, RevisionType revisionType,
							  Object revisionEntity) {
		final var revision = (AuditEnversRevision) revisionEntity;
	}
}
