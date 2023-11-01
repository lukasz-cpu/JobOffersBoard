package com.lukaszpopek.JobOffersBoard.scrapper.controller;


import java.util.List;
import lombok.Builder;

@Builder
public record JobOfferDTO(String id,
                          String jobTitle,
                          String companyName,
                          List<String> jobCategory,
                          List<String> seniority,
                          List<String> mustHaveRequirements,
                          List<String> niceHaveRequirements,
                          SalaryRangeDTO salaryRangeDTO,
                          String location) {

}