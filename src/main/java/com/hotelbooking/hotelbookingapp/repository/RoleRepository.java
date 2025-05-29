package com.hotelbooking.hotelbookingapp.repository;

import com.hotelbooking.hotelbookingapp.model.Role;
import com.hotelbooking.hotelbookingapp.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleType(RoleType roleType);
}
