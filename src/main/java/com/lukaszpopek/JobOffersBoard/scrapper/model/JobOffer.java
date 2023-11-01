package com.lukaszpopek.JobOffersBoard.scrapper.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class JobOffer {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  @NotNull
  private UUID id;
  private String jobTitle;
  private String companyName;
  private List<String> jobCategory;
  private List<String> seniority;
  private List<String> mustHaveRequirements;
  private List<String> niceHaveRequirements;
  @Embedded
  @Builder.Default
  private SalaryRange salaryRange = SalaryRange.builder().build();
  private String location;
  @OneToOne(mappedBy = "jobOffer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  private JobLink jobLink;

}


