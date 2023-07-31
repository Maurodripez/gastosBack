package com.mauricio.gastos.service;

import com.mauricio.gastos.DTO.RoleDTO;
import com.mauricio.gastos.models.ERole;
import com.mauricio.gastos.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Override
    public List<RoleDTO> getRoles() {
        ERole[] roles = ERole.values();
        List<RoleDTO> rolesList = new ArrayList<>();

        for (ERole role : roles) {
            rolesList.add(new RoleDTO(role.name()));
        }
        return rolesList;
    }
}
