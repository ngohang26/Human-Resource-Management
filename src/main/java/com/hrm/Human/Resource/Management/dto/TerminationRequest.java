package com.hrm.Human.Resource.Management.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

    @Getter
    @Setter
    public class TerminationRequest {
        private Long reasonId;
        private LocalDate terminationDate;
    }
