package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.RequestGroupService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRequestGroupServiceRepository extends JpaRepository<RequestGroupService, Long> {
    RequestGroupService findRequestGroupById(Long id);
}
