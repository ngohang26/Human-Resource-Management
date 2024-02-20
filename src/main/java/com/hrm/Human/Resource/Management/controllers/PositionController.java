package com.hrm.Human.Resource.Management.controllers;

import com.hrm.Human.Resource.Management.entity.Position;
import com.hrm.Human.Resource.Management.entity.Position;
import com.hrm.Human.Resource.Management.repositories.PositionRepositories;
import com.hrm.Human.Resource.Management.response.PositionResponse;
import com.hrm.Human.Resource.Management.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/positions")
public class PositionController {
        @Autowired
        private PositionRepositories positionRepositories;
        
        @Autowired
        private PositionService positionService;
    
        @GetMapping(path = "getAllPositions")
        public List<Position> getAllPositions() {
            return positionService.getPositions();
        }
    
        @GetMapping(path = "/{id}")
        public ResponseEntity<Position> getPositionById(@PathVariable Long id) {
            Optional<Position> position = positionService.findById(id);
            return position.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        }
    
        @PostMapping("/addPosition")
        public ResponseEntity<?> addPosition(@RequestBody Position position) {
            return positionService.addPosition(position);
        }
    
        @PutMapping("/#/{id}")
        public Position updatePosition(@PathVariable Long id, @RequestBody Position positionDetails) {
            return positionService.updatePosition(id, positionDetails);
        }


        @DeleteMapping("/hardDelete/{id}")
        public ResponseEntity<PositionResponse> hardDeletePosition(@PathVariable Long id) {
            return positionService.hardDeletePosition(id);
        }
}