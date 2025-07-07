package com.app.boilerplate.Util;

import com.app.boilerplate.Domain.Common.AuditEnversRevision;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ApplicationRevisionListener implements EntityTrackingRevisionListener {
	@Override
	public void newRevision(final Object revisionEntity) {
		final var revision = (AuditEnversRevision) revisionEntity;
		if (SecurityUtil.isAnonymous()){
			revision.setUserId(AppConsts.SYSTEM_USER_ID);
		}else{
			revision.setUserId(SecurityUtil.getAccessJwt().getSub());
		}
	}

	@Override
	public void entityChanged(Class entityClass, String entityName,
							  Object entityId, RevisionType revisionType,
							  Object revisionEntity) {
		final var revision = (AuditEnversRevision) revisionEntity;
	}
}
