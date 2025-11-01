package khoindn.swp391.be.app.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.model.Request.EmailDetailReq;
import khoindn.swp391.be.app.model.Request.SendBulkEmailReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Transactional
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailDetailReq contentSender) {
        try {
            // MimeMessage cho phép gửi HTML + file đính kèm
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            // true = multipart message (cho phép đính kèm file)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(contentSender.getEmail());
            helper.setSubject(contentSender.getSubject());
            helper.setText(contentSender.getTemplate(), true); // true = nội dung HTML

            // Nếu có file đính kèm
            if (contentSender.getUrl() != null) {
                FileSystemResource file = new FileSystemResource(new File(contentSender.getUrl()));
                helper.addAttachment(file.getFilename(), file);
            }

            javaMailSender.send(mimeMessage);
            System.out.println("✅ Email sent successfully to " + contentSender.getEmail());

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void SendBulkEmail(SendBulkEmailReq emailReq) {
        for (String eachEmail : emailReq.getEmail()) {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(eachEmail);
                helper.setSubject("[EcoShare System] E-Contract");
                helper.setText(
                        "<a href='" + emailReq.getContent() +
                                "'>Nhấn vào đây để xem hợp đồng</a>", true);

                javaMailSender.send(message);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }
}
