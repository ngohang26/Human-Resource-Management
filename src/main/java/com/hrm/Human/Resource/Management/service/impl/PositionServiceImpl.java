package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Position;
import com.hrm.Human.Resource.Management.entity.PersonalInfo;
import com.hrm.Human.Resource.Management.repositories.PositionRepositories;
import com.hrm.Human.Resource.Management.repositories.PersonalInfoRepositories;
import com.hrm.Human.Resource.Management.response.PositionResponse;
import com.hrm.Human.Resource.Management.service.PositionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositionServiceImpl implements PositionService {
    @Autowired
    private PositionRepositories positionRepositories;

    @Override
    public Optional<Position> findById(Long id) {
        return positionRepositories.findById(id);
    }

    @Override
    public List<Position> getPositions() {return positionRepositories.findAll();}

    @Override
    public ResponseEntity<?> addPosition(Position position) {
        Optional<Position> existingPosition = positionRepositories.findByPositionNameContaining(position.getPositionName());
        if (existingPosition.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The position already exists.");
        }

        try {
            Position savedPosition = positionRepositories.save(position);
            return new ResponseEntity<>(savedPosition, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Position updatePosition(Long id, Position updatedPosition) {
        Position existingPosition = positionRepositories.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id " + id));

        existingPosition.setPositionName(updatedPosition.getPositionName());

        return positionRepositories.save(existingPosition);
    }

    @Override
    public ResponseEntity<PositionResponse> hardDeletePosition(Long id){
        boolean exists = positionRepositories.existsById(id);
        if (exists) {
            positionRepositories.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new PositionResponse("ok", "Delete position successfully", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new PositionResponse("failed", "Cannot find position to delete", "")
        );
    }
}
