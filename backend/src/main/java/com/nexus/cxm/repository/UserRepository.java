package com.nexus.cxm.repository;

import com.nexus.cxm.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByTeamId(Long teamId);
    List<User> findByIsActiveTrue();
}
