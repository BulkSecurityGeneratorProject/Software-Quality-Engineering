package com.acme.repository;

import com.acme.domain.ACMEPass;

import org.springframework.data.jpa.repository.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Spring Data JPA repository for the ACMEPass entity.
 */
@SuppressWarnings("unused")
public interface ACMEPassRepository extends JpaRepository<ACMEPass, Long> {

	@Query("select acmePass from ACMEPass acmePass where acmePass.user.login = ?#{principal.username}")
	Page<ACMEPass> findByUserIsCurrentUser(Pageable pageable);
}
