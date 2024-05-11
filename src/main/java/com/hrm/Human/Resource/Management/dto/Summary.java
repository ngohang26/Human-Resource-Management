package com.hrm.Human.Resource.Management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Summary {
    private long candidateCount;
    private long employeeCount;
    private long userCount;
}
