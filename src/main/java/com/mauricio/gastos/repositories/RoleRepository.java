package com.mauricio.gastos.repositories;

import com.mauricio.gastos.models.ERole;
import com.mauricio.gastos.models.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    @Query("SELECT DISTINCT r.name FROM RoleEntity r")
    List<ERole> findDistinctRoleNames();
}
