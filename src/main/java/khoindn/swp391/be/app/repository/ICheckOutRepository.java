package khoindn.swp391.be.app.repository;

import khoindn.swp391.be.app.pojo.CheckOut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICheckOutRepository extends JpaRepository<CheckOut, Integer> {

    boolean existsBySchedule_ScheduleId(int scheduleScheduleId);

    CheckOut findByScheduleScheduleId(int scheduleScheduleId);
}
