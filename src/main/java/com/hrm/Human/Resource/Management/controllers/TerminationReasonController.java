package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Allowance;
import com.hrm.Human.Resource.Management.entity.TerminationReason;
import com.hrm.Human.Resource.Management.service.impl.TerminationReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/terminationReasons")
public class TerminationReasonController {
    @Autowired
    private TerminationReasonService terminationReasonService;

    @GetMapping("/getAllTerminationReason")
    public List<TerminationReason> getAllTerminationReason() {return terminationReasonService.getAllTerminationReason();}

    @PostMapping("/addTerminationReason")
    public ResponseEntity<TerminationReason> createTerminationReason(@RequestBody TerminationReason terminationReason) {
        return ResponseEntity.ok(terminationReasonService.createTerminationReason(terminationReason));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TerminationReason> updateTerminationReason(@PathVariable Long id, @RequestBody TerminationReason terminationReason) {
        return ResponseEntity.ok(terminationReasonService.updateTerminationReason(id, terminationReason));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTerminationReason(@PathVariable Long id) {
        terminationReasonService.deleteTerminationReason(id);
        return ResponseEntity.ok().build();
    }
}

