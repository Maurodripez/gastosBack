package com.mauricio.gastos.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.mauricio.gastos.models.UserEntity;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsernameAndIdNot(String username, Long id);

	boolean existsByEmailAndIdNot(String email, Long id);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN false ELSE true END FROM UserEntity u WHERE u.username = :username AND verify = false")
	boolean isVerify(String username);


	@Transactional
	@Modifying
	@Query("UPDATE UserEntity u SET u.verify = :verify WHERE u.username = :username")
	void changeVerify(String username, boolean verify);

	@Query("SELECT u.email FROM UserEntity u WHERE u.username = :username")
	Optional<String> findEmailByUsername(String username);

	@Query("SELECT COUNT(u) FROM UserEntity u WHERE u.username = :username AND verify = true")
	int emailIsVerify(String username);


}
