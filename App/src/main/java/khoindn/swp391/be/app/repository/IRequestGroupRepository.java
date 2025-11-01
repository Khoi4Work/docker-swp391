package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.RequestGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRequestGroupRepository extends JpaRepository<RequestGroup, Long> {
    RequestGroup findRequestGroupById(Long id);
}
