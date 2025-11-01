package khoindn.swp391.be.app.service;

import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class KnowledgeSearchService {

    private final JdbcTemplate jdbcTemplate;

    public KnowledgeSearchService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Tìm topK context liên quan (MVP).
     * - Không phân biệt dấu/hoa thường với Vietnamese_CI_AI.
     * - Ưu tiên match ở title, rồi đến content.
     * - Clamp topK để an toàn driver với TOP(...) và hiệu năng.
     */
    public List<String> searchRelated(String question, int topK) {
        String q = (question == null) ? "" : question.trim();
        if (q.isEmpty()) return List.of();

        // clamp topK (1..20)
        int k = Math.max(1, Math.min(topK, 20));

        // Lưu ý: TOP (?) có thể lỗi trên một số driver → nối trực tiếp con số k (đã clamp).
        String sql = """
            SELECT TOP (%d)
                CAST(
                    COALESCE(title, '') +
                    CASE WHEN title IS NOT NULL AND content IS NOT NULL THEN ': ' ELSE '' END +
                    COALESCE(content, '')
                AS NVARCHAR(MAX)) AS text
            FROM knowledge_base
            WHERE
                -- Ưu tiên title
                (title   COLLATE Vietnamese_CI_AI LIKE '%%' + ? + '%%')
                OR
                (content COLLATE Vietnamese_CI_AI LIKE '%%' + ? + '%%')
            ORDER BY
                CASE WHEN title   COLLATE Vietnamese_CI_AI LIKE '%%' + ? + '%%' THEN 0 ELSE 1 END,
                CASE WHEN content COLLATE Vietnamese_CI_AI LIKE '%%' + ? + '%%' THEN 0 ELSE 1 END,
                id DESC
            """.formatted(k);

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("text"),
                q, q, q, q
        );
    }

    /* ================== Nâng cấp FULLTEXT (khuyến nghị) ==================
       1) Chạy 1 lần (yêu cầu có PK clustered trên id):
          CREATE FULLTEXT CATALOG ftCat AS DEFAULT;
          CREATE FULLTEXT INDEX ON dbo.knowledge_base(title, content)
          KEY INDEX PK_knowledge_base WITH STOPLIST = SYSTEM;

       2) Dùng FREETEXT (semantic rộng) hoặc CONTAINSTABLE (truy vấn chính xác hơn).
       3) Ưu tiên theo FT.RANK, rồi id DESC.
    */
    public List<String> searchRelatedFulltext(String question, int topK) {
        String q = (question == null) ? "" : question.trim();
        if (q.isEmpty()) return List.of();
        int k = Math.max(1, Math.min(topK, 20));

        String sql = """
            SELECT TOP (%d)
                   CAST(COALESCE(KI.title,'') + 
                        CASE WHEN KI.title IS NOT NULL AND KI.content IS NOT NULL THEN ': ' ELSE '' END +
                        COALESCE(KI.content,'')
                   AS NVARCHAR(MAX)) AS text
            FROM FREETEXTTABLE(dbo.knowledge_base, (title, content), ?) FT
            JOIN dbo.knowledge_base KI ON KI.id = FT.[KEY]
            ORDER BY FT.RANK DESC, KI.id DESC
            """.formatted(k);

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("text"),
                q
        );
    }

    /* (Tùy chọn) Nếu muốn “tìm đúng cụm” hơn với FULLTEXT:
       CONTAINSTABLE(dbo.knowledge_base, (title, content), ' "cụm từ" OR từ1 NEAR từ2 ')
    */
}
