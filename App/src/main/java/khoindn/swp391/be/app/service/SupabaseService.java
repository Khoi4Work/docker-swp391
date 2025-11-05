package khoindn.swp391.be.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SupabaseService implements ISupabaseService {


    @Value("${SUPABASE_URL}")
    private String SUPABASE_URL;
    @Value("${SUPABASE_KEY}")
    private String SUPABASE_KEY;
    @Value("${SUPABASE_PUBLIC_URL}")
    private String SUPABASE_PUBLIC_URL;
    @Value("${SUPABASE_URL_LIST}")
    private String SUPABASE_URL_LIST;


    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename(); // giữ tên file gốc
        System.out.println(fileName);
        if (isFileExist(fileName)) {
            throw new RuntimeException("file is existed! and now Deleted!");
        }
        URL url = new URL(SUPABASE_URL + fileName);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
        conn.setRequestProperty("Content-Type", file.getContentType());

        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                conn.getOutputStream().write(buffer, 0, bytesRead);
            }
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            // trả public link nếu bucket public

            return SUPABASE_PUBLIC_URL + fileName;
        } else {
            throw new RuntimeException("Upload thất bại, code: " + responseCode);
        }
    }

    public boolean isFileExist(String fileName) {
        try {
            URL url = new URL(SUPABASE_PUBLIC_URL + fileName);
            System.out.println(url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int code = conn.getResponseCode();
            return code == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public String getFileUrl(String fileName) {
        // Chỉ cần nối SUPABASE_LINK_URL với filename nếu bucket public
        return SUPABASE_PUBLIC_URL + fileName;
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        String bucket = "uploadsPDF";

        URL url = new URL(SUPABASE_URL+bucket);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
        conn.setRequestProperty("apikey", SUPABASE_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Body phải là mảng JSON chứa đường dẫn file (tương đối trong bucket)
        String jsonBody = "[\"" + fileName + "\"]";

        try (var os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200 && responseCode != 204) {
            throw new RuntimeException("Xóa file thất bại, code: " + responseCode);
        } else {
            System.out.println("✅ File deleted successfully: " + fileName);
        }
    }


}
