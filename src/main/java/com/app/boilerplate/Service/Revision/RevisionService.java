package com.app.boilerplate.Service.Revision;

import com.app.boilerplate.Domain.Common.AuditEnversRevision;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class RevisionService {
    private final EntityManager entityManager;

    public LocalDateTime getLastChangeDateForField(Object entityId, String fieldName, Class<?> clazz) {
        final var auditReader = AuditReaderFactory.get(entityManager);

        @SuppressWarnings("unchecked")
        final List<Object[]> result = auditReader.createQuery()
            .forRevisionsOfEntity(clazz, false, true)
            .add(AuditEntity.id().eq(entityId))
            .add(AuditEntity.property(fieldName).hasChanged())
            .addOrder(AuditEntity.revisionNumber().desc())
            .setMaxResults(1)
            .getResultList();

        if (!result.isEmpty()) {
            final var revisionData = result.get(0);
            if (revisionData.length > 1 && revisionData[1] instanceof AuditEnversRevision rev) {
                return rev.getTimestamp();
            }
        }

        return null;
    }
}