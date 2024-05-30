package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.TerminationReason;
import com.hrm.Human.Resource.Management.repositories.TerminationReasonRepositories;
import com.hrm.Human.Resource.Management.response.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TerminationReasonService {
    @Autowired
    private TerminationReasonRepositories terminationReasonRepositories;

    public List<TerminationReason> getAllTerminationReason() {return terminationReasonRepositories.findAll();}
    public TerminationReason createTerminationReason(TerminationReason terminationReason) {
        Optional<TerminationReason> existingReason = terminationReasonRepositories.findByReason(terminationReason.getReason());
        if (existingReason.isPresent()) {
            throw new IllegalArgumentException("Lý do nghỉ việc đã tồn tại: " + terminationReason.getReason());
        }
        return terminationReasonRepositories.save(terminationReason);
    }

    public TerminationReason updateTerminationReason(Long id, TerminationReason terminationReason) {
        TerminationReason existingReason = terminationReasonRepositories.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lý do nghỉ việc với id: " + id));
        existingReason.setReason(terminationReason.getReason());
        return terminationReasonRepositories.save(existingReason);
    }

    public void deleteTerminationReason(Long id) {
        TerminationReason existingReason = terminationReasonRepositories.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lý do nghỉ việc với id: " + id));
        terminationReasonRepositories.delete(existingReason);
    }
}

