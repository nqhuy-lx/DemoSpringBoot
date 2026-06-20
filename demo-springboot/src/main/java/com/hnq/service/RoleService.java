package com.hnq.service;

import com.hnq.model.Role;
import com.hnq.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> getAllByUserId(Long userId) {
        return roleRepository.getAllByUserId(userId);
    }
}
