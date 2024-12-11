package com.advancia.stage.SpringBootThymeleaf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.advancia.stage.SpringBootThymeleaf.entity.Tutorial;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TutorialRepository extends JpaRepository<Tutorial, Integer> {
	List<Tutorial> findByTitleContainingIgnoreCase(String keyword);

	@Query("UPDATE Tutorial t SET t.published = :published WHERE t.id = :id")
	@Modifying
	public void updatePublishedStatus(Integer id, boolean published);
}
