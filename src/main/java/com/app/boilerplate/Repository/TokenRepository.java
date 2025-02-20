package com.app.boilerplate.Repository;

import com.app.boilerplate.Domain.User.Token;
import com.app.boilerplate.Domain.User.User;
import com.app.boilerplate.Shared.Authentication.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
	Optional<Token> findByTypeAndValue(TokenType type, String value);
	List<Token> findAllByTypeAndUser(TokenType type, User user);

	boolean existsByValue(String value);

	void deleteByExpireDateBefore(LocalDateTime date);
	void deleteByValue(String value);
	void deleteTokenByTypeAndUser(TokenType tokenType, User user);
}
