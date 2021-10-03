package com.intalio.intaliosyed.repository;

import com.intalio.intaliosyed.entity.Role;
import com.intalio.intaliosyed.model.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Collection<Role> findByNameIn(Collection<ERole> name);
}
