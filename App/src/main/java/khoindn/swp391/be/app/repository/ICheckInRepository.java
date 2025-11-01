package khoindn.swp391.be.app.repository;


import khoindn.swp391.be.app.pojo.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICheckInRepository extends JpaRepository<CheckIn, Integer> {
    CheckIn findByScheduleScheduleId(int scheduleScheduleId);

    boolean existsBySchedule_ScheduleId(int scheduleScheduleId);
}
