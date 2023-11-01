package com.lukaszpopek.JobOffersBoard.scrapper.repository;

import com.lukaszpopek.JobOffersBoard.scrapper.model.JobOffer;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOfferRepository extends JpaRepository<JobOffer, UUID> {

  Page<JobOffer> findByJobCategory(String category, Pageable pageable);
}
