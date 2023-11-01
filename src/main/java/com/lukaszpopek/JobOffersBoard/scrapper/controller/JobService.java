package com.lukaszpopek.JobOffersBoard.scrapper.controller;

import com.lukaszpopek.JobOffersBoard.scrapper.model.JobOffer;
import com.lukaszpopek.JobOffersBoard.scrapper.repository.JobOfferRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class JobService {

  private JobOfferRepository jobOfferRepository;

  public List<JobOffer> getJobs(int page, int pageSize, String category) {
    Pageable pageable = PageRequest.of(page, pageSize);
    Page<JobOffer> jobPage;
    if (category != null) {
      jobPage = jobOfferRepository.findByJobCategory(category, pageable);
    } else {
      jobPage = jobOfferRepository.findAll(pageable);
    }
    return jobPage.getContent();
  }
}
