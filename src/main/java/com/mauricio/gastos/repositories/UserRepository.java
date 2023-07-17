package com.mauricio.gastos.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mauricio.gastos.models.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByUsername(String username);

	@Query("SELECT respuesta FROM UserEntity respuesta WHERE respuesta.username = ?1")
	Optional<UserEntity> getName(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsernameAndIdNot(String username, Long id);

	boolean existsByEmailAndIdNot(String email, Long id);
}
