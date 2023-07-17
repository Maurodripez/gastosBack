package com.mauricio.gastos.service;

import com.mauricio.gastos.DTO.RoleDTO;
import com.mauricio.gastos.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Override
    public List<RoleDTO> getRoles() {
        return roleRepository.findDistinctRoleNames().stream()
                .map(role -> RoleDTO.builder()
                        .role(role.name())
                        .build())
                .collect(Collectors.toList());
    }
}
