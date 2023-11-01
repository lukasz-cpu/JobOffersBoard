package com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service;

import static com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingHtmlUtils.getCategories;
import static com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingHtmlUtils.getCompanyName;
import static com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingHtmlUtils.getJobTitle;
import static com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingHtmlUtils.getLocation;
import static com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingHtmlUtils.getMustHaveRequirements;
import static com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingHtmlUtils.getNiceToHaveRequirements;
import static com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingHtmlUtils.getSalary;
import static com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingHtmlUtils.getSeniority;

import com.lukaszpopek.JobOffersBoard.scrapper.model.JobLink;
import com.lukaszpopek.JobOffersBoard.scrapper.model.JobOffer;
import com.lukaszpopek.JobOffersBoard.scrapper.repository.JobLinkRepository;
import com.lukaszpopek.JobOffersBoard.scrapper.repository.JobOfferRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ScrappingService {

  private final JobOfferRepository jobOfferRepository;
  private final JobLinkRepository jobLinkRepository;
  @Value("${job.word}")
  private String jobWord;

  public ScrappingService(JobOfferRepository jobOfferRepository,
      JobLinkRepository jobLinkRepository) {
    this.jobOfferRepository = jobOfferRepository;
    this.jobLinkRepository = jobLinkRepository;
  }

  @Transactional(rollbackFor = {PSQLException.class})
  public void saveLink(String singleLink) {
    JobOffer jobOffer =
        jobOfferRepository.save(JobOffer.builder().id(UUID.randomUUID()).build());

    JobLink jobLink = JobLink.builder()
        .jobLink(singleLink)
        .jobOffer(jobOffer)
        .isProcessed(false)
        .category(jobWord)
        .id(UUID.randomUUID())
        .timeOfAddition(LocalDateTime.now())
        .remote(true)
        .build();
    jobLinkRepository.save(jobLink);
  }

  @Transactional
  public void saveJobOffer(String link, Document doc) {
    JobLink jobLink = jobLinkRepository.findByJobLink(link);
    if (jobLink.getJobOffer() != null && !jobLink.isProcessed()) {
      UUID jobOfferId = jobLink.getJobOffer().getId();
      JobOffer jobOffer = jobOfferRepository.getReferenceById(jobOfferId);
      jobOffer.setJobTitle(getJobTitle(doc));
      jobOffer.setCompanyName(getCompanyName(doc));
      jobOffer.setJobCategory(getCategories(doc));
      jobOffer.setSeniority(getSeniority(doc));
      jobOffer.setMustHaveRequirements(getMustHaveRequirements(doc));
      jobOffer.setNiceHaveRequirements(getNiceToHaveRequirements(doc));
      jobOffer.setSalaryRange(getSalary(doc));
      jobOffer.setLocation(getLocation(doc));
      jobOfferRepository.save(jobOffer);
      jobLink.setProcessed(true);
      jobLinkRepository.save(jobLink);
    }
  }
}
