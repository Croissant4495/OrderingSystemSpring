package com.ejada.project.model;

import java.util.HashSet;
import java.util.Set;

import com.ejada.project.enums.RoleName;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(
        name = "role_seq",
        sequenceName = "ROLE_SEQ",
        allocationSize = 1)    
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
