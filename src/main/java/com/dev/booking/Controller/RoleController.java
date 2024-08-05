package com.dev.booking.Controller;

import com.dev.booking.Entity.Movie;
import com.dev.booking.Entity.Role;
import com.dev.booking.Repository.RoleRepository;
import com.dev.booking.ResponseDTO.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/roles")
@CrossOrigin(origins = "*")
public class RoleController {
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<Role>>> getAll(){
        List<Role> roleList = roleRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<List<Role>>("",roleList));
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ResponseObject<Role>> getById(@PathVariable Long id){
        Role role = roleRepository.findById(id).orElse(null);
        if(role != null){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<Role>("",role));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Role>("id does not exist",role));
    }

}
