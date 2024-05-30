package com.hrm.Human.Resource.Management.service.impl;

import com.hrm.Human.Resource.Management.entity.Department;
import com.hrm.Human.Resource.Management.entity.Position;
import com.hrm.Human.Resource.Management.repositories.DepartmentRepositories;
import com.hrm.Human.Resource.Management.repositories.EmployeeRepositories;
import com.hrm.Human.Resource.Management.repositories.PositionRepositories;
import com.hrm.Human.Resource.Management.response.ErrorResponse;
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

    @Autowired
    private EmployeeRepositories employeeRepositories;

    @Override
    public Optional<Position> findById(Long id) {
        return positionRepositories.findById(id);
    }

    @Override
    public List<Position> getPositions() {

        return positionRepositories.findAll();
    }

    @Autowired
    private DepartmentRepositories departmentRepositories;
    @Override
    public Position findPositionByName(String positionName) {
        return positionRepositories.findByPositionName(positionName);
    }

    @Override
    public ResponseEntity<?> addPosition(Position position) {
        Optional<Position> existingPosition = positionRepositories.findByPositionNameContaining(position.getPositionName());
        if (existingPosition.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("The position already exists.");
        }

        Department department = departmentRepositories.findById(position.getDepartment().getId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id " + position.getDepartment().getId()));
        position.setDepartment(department);

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

        Optional<Position> positionWithNewName = positionRepositories.findByPositionNameContaining(updatedPosition.getPositionName());
        if (positionWithNewName.isPresent() && !positionWithNewName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Position already exists.");
        }

        Department department = departmentRepositories.findById(updatedPosition.getDepartment().getId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id " + updatedPosition.getDepartment().getId()));
        existingPosition.setDepartment(department);


        existingPosition.setPositionName(updatedPosition.getPositionName());
        existingPosition.setJobSummary(updatedPosition.getJobSummary());
        return positionRepositories.save(existingPosition);
    }


    @Override
    public ResponseEntity<ErrorResponse> deletePosition(Long id) {
        if (!positionRepositories.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponse("failed", "Không tìm thấy vị trí để xóa", "")
            );
        }
        // Kiểm tra xem có bản ghi nào trong bảng 'employees' tham chiếu đến 'position_id' này không
        if (employeeRepositories.existsByPositionId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse("failed", "Không thể xóa vị trí vì nó đang được tham chiếu bởi một nhân viên", "")
            );
        }
        positionRepositories.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ErrorResponse("ok", "Xóa vị trí thành công", "")
        );
    }

}
