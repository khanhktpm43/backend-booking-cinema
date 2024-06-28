package com.dev.booking.Controller;

import com.dev.booking.Entity.Cast;
import com.dev.booking.Repository.CastRepository;
import com.dev.booking.ResponseDTO.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/cast")
public class Castcontroller {
    @Autowired
    private CastRepository castRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<Cast>>> getAll(){
        List<Cast> casts = castRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<List<Cast>>("",casts));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<Cast>> getById(@PathVariable Long id){
        if(castRepository.existsById(id)){
            Cast cast = castRepository.findById(id).orElse(null);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<Cast>("",cast));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Cast>("id does not exist",null));
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject<Cast>> create(@RequestBody Cast cast){
        if(!castRepository.existsByName(cast.getName())){
            cast.setId(null);
            Cast newCast = castRepository.save(cast);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject<Cast>("",newCast));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject<Cast>("name does exist",null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Cast>> update(@PathVariable Long id, @RequestBody Cast cast){
        if(castRepository.existsById(id)){
            cast.setId(id);
            Cast newCast = castRepository.save(cast);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<Cast>("", newCast));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Cast>("id does not exist", null));
    }
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseObject<Cast>> delete(@PathVariable Long id){
        if(castRepository.existsById(id)){
            castRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject<Cast>("", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject<Cast>("id does not exist",null));
    }
}
