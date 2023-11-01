package com.lukaszpopek.JobOffersBoard.scrapper.repository;

import com.lukaszpopek.JobOffersBoard.scrapper.model.JobLink;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLinkRepository extends JpaRepository<JobLink, UUID> {

  JobLink findByJobLink(String string);
}
