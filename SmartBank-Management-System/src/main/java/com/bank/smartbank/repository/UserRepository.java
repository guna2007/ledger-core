package com.bank.smartbank.repository;

import com.bank.smartbank.entity.Role;
import com.bank.smartbank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	List<User> findByIsVerifiedTrue();

	List<User> findByRole(Role role);

}
