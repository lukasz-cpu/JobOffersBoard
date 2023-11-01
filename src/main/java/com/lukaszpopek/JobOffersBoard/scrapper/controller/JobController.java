package com.lukaszpopek.JobOffersBoard.scrapper.controller;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.google.gson.Gson;
import com.lukaszpopek.JobOffersBoard.scrapper.model.JobOffer;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = {"/api/v1/jobs"}, produces = APPLICATION_JSON_VALUE)
public class JobController {


  private final JobService jobService;
  private final Gson gson = new Gson();

  @GetMapping
  public ResponseEntity<?> getJobs(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String category) {
    List<JobOffer> jobs = jobService.getJobs(page, pageSize, category);
    List<JobOfferDTO> jobOfferDTOS = jobs.stream().map(this::mapToDTO).toList();
    String jsonResponse = gson.toJson(jobOfferDTOS);
    return ResponseEntity.of(Optional.of(jsonResponse));
  }

  private JobOfferDTO mapToDTO(JobOffer job) {
    return JobOfferDTO.builder()
        .id(job.getId().toString())
        .companyName(job.getCompanyName())
        .jobCategory(job.getJobCategory())
        .seniority(job.getSeniority())
        .mustHaveRequirements(job.getMustHaveRequirements())
        .niceHaveRequirements(job.getNiceHaveRequirements())
        .salaryRangeDTO(SalaryRangeDTO
            .builder()
            .min(job.getSalaryRange().getLowerSalary())
            .max(job.getSalaryRange().getHighSalary())
            .build())
        .location(job.getLocation())
        .build();
  }
}
