package khoindn.swp391.be.app.service;

import java.util.List;
import khoindn.swp391.be.app.pojo.Message;
import khoindn.swp391.be.app.model.Response.LlmResult;


public interface LlmClientService {
    LlmResult chat(List<Message> messages);
}
