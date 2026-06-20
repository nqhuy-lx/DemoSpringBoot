package com.hnq.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_group")
public class Group extends AbstractEntity<Integer> {
    private String name;

    @OneToOne
    private Role role;

    @OneToMany(mappedBy = "group")
    private Set<UserHasGroup> users =  new HashSet<>();
}
