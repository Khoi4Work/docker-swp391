package khoindn.swp391.be.app.controller;

import jakarta.validation.Valid;
import khoindn.swp391.be.app.model.Request.EmailDetailReq;
import khoindn.swp391.be.app.model.Request.SendBulkEmailReq;
import khoindn.swp391.be.app.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:8081")
public class EmailController {

    @Autowired
    private IEmailService  iEmailService;

    @PostMapping("/send")
    public ResponseEntity sendEmail(@RequestBody EmailDetailReq contentSender) {
        System.out.println(contentSender);
        iEmailService.sendEmail(contentSender);
        return  ResponseEntity.ok().body("Send email successfully");
    }

    // Gửi email
    @PostMapping("/bulk/send")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid SendBulkEmailReq emailReq) {
        try {
            iEmailService.SendBulkEmail(emailReq);
            return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }
}
