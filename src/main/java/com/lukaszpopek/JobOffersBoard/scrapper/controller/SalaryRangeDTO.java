package com.lukaszpopek.JobOffersBoard.scrapper.controller;

import lombok.Builder;

@Builder
public record SalaryRangeDTO(int min, int max) {

}
