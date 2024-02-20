package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.TimeOffType;

public interface TimeOffTypeService {
    TimeOffType addTimeOffType(TimeOffType timeOffType);

    TimeOffType updateTimeOffType(Long id, TimeOffType timeOffType);

    void deleteTimeOffType(Long id);
}
