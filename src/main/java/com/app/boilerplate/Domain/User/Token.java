package com.app.boilerplate.Domain.User;

import com.app.boilerplate.Shared.Authentication.TokenType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "token", indexes = {
	@Index(name = "idx_token_value", columnList = Token_.VALUE)
})
public class Token implements Serializable {
    @Serial
    private static final long serialVersionUID = 3163366597256193499L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "INT UNSIGNED", updatable = false)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false, columnDefinition = "SMALLINT", updatable = false)
    private TokenType type;

    @Column(name = "value", length = 40, unique = true, nullable = false)
    private String value;

    @Column(name = "expire_date", nullable = false, updatable = false)
    private LocalDateTime expireDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}

