package com.hrm.Human.Resource.Management.service;

import com.hrm.Human.Resource.Management.entity.Position;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PositionService {
    List<Position> getPositions();

    Optional<Position> findById(Long id);

    ResponseEntity<?> addPosition(Position position);

    Position updatePosition(Long id, Position positionDetails);


    ResponseEntity<ErrorResponse> deletePosition(Long id);

    Position findPositionByName(String positionName);
}
