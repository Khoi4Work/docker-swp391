package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Response.LlmResult;
import khoindn.swp391.be.app.pojo.Message;

import java.util.List;


public interface ILlmClientService {
    LlmResult chat(List<Message> messages);
}
