package com.acme.repository;

import com.acme.domain.Comment;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByPostId(Long postId);
}
