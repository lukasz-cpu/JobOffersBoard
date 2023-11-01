package com.lukaszpopek.JobOffersBoard.scrapper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class JobLink {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  @NotNull
  private UUID id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_offer_id")
  private JobOffer jobOffer;

  @Column(unique = true)
  private String jobLink;

  private String category;

  private boolean remote;

  private boolean isProcessed;

  private LocalDateTime timeOfAddition;

}
