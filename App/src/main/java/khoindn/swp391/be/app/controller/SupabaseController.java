package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import khoindn.swp391.be.app.service.SupabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supabase")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
@SecurityRequirement(name = "api")
public class SupabaseController {

    @Autowired
    private SupabaseService supabaseService;

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(String fileName) throws Exception {
        supabaseService.deleteFile(fileName);
        return new ResponseEntity<>("delete successfully", HttpStatus.OK);
    }
}
