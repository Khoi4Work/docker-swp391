package khoindn.swp391.be.app.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import khoindn.swp391.be.app.model.Request.ChatRequest;
import khoindn.swp391.be.app.model.Response.ChatResponse;
import khoindn.swp391.be.app.service.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/chat", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "api")
@CrossOrigin(origins = "*") // adjust for your front-end domain if needed
public class ChatController {

    @Autowired
    private ChatGPTService chatGPTService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = chatGPTService.generateReply(request);
        return ResponseEntity.ok(response);
    }
}
