package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.EmailDetailReq;
import khoindn.swp391.be.app.model.Request.SendBulkEmailReq;

public interface IEmailService {

    public void sendEmail(EmailDetailReq sender);
    public void SendBulkEmail(SendBulkEmailReq emailReq);
}
