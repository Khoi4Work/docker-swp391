package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<Users, Integer> {
    public Users findByEmail(String email);

    Users findUsersById(Integer id);
}
