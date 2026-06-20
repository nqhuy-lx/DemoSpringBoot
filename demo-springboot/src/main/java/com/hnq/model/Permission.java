package com.hnq.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_permission")
public class Permission extends AbstractEntity<Integer> {
    private String name;

    @OneToMany(mappedBy = "permission")
    private Set<RoleHasPermission> roles = new HashSet<>();
}
