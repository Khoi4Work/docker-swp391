package khoindn.swp391.be.app.service;

import org.springframework.web.multipart.MultipartFile;

public interface ISupabaseService {
    String uploadFile(MultipartFile file) throws Exception;
    String getFileUrl(String fileName);
    void deleteFile(String fileName) throws Exception;
}
