package khoindn.swp391.be.app.service;

import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.exception.exceptions.*;
import khoindn.swp391.be.app.model.Request.EmailDetailReq;
import khoindn.swp391.be.app.model.Request.ScheduleReq;
import khoindn.swp391.be.app.model.Response.OverrideInfoRes;
import khoindn.swp391.be.app.model.Response.ScheduleRes;
import khoindn.swp391.be.app.model.Response.VehicleRes;
import khoindn.swp391.be.app.pojo.*;
import khoindn.swp391.be.app.pojo._enum.StatusSchedule;
import khoindn.swp391.be.app.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService implements IScheduleService {
    @Autowired
    private IScheduleRepository iScheduleRepository;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private IGroupMemberRepository iGroupMemberRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IUserRepository iUserRepository;
    @Autowired
    private IGroupRepository iGroupRepository;
    @Autowired
    private IVehicleRepository iVehicleRepository;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public ScheduleRes createSchedule(ScheduleReq req) {
        // Validate time range
        validateTimeRange(req.getStartTime(), req.getEndTime());

        Users user = iUserRepository.findById(req.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Group group = iGroupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        GroupMember gm = iGroupMemberRepository.findByGroupAndUsers(group, user)
                .orElseThrow(() -> new UserNotBelongException("User does not belong to this group"));

        Vehicle vehicle = iVehicleRepository.findVehicleByVehicleId(req.getVehicleId());
        if (vehicle == null || vehicle.getGroup().getGroupId() != req.getGroupId()) {
            throw new VehicleNotBelongException("Vehicle does not belong to this group");
        }

        checkTimeConflicts(req.getGroupId(), req.getStartTime(), req.getEndTime(), gm, user, null);


        Schedule schedule = new Schedule();
        schedule.setStartTime(req.getStartTime());
        schedule.setEndTime(req.getEndTime());
        schedule.setStatus(StatusSchedule.BOOKED);
        schedule.setGroupMember(gm);

        Schedule saved = iScheduleRepository.save(schedule);

        ScheduleRes res = modelMapper.map(saved, ScheduleRes.class);
        res.setUserId(user.getId());
        res.setGroupId(group.getGroupId());
        res.setVehicleId(vehicle.getVehicleId());

        return res;
    }


    @Override
    public List<ScheduleRes> getAllSchedules() {
        return iScheduleRepository.findAll().stream()
                .filter(s -> !s.getStatus().equals(StatusSchedule.OVERRIDE_TRACKER))
                .map(s -> {
                    ScheduleRes res = modelMapper.map(s, ScheduleRes.class);
                    res.setUserId(s.getGroupMember().getUsers().getId());
                    res.setGroupId(s.getGroupMember().getGroup().getGroupId());
                    Vehicle vehicle = iVehicleRepository.findByGroup(s.getGroupMember().getGroup());
                    if (vehicle != null) {
                        res.setVehicleId(vehicle.getVehicleId());
                    }
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleRes> getCarsByGroupIdAndUserId(int groupId, int userId) {
        Users user = iUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Group group = iGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        iGroupMemberRepository.findByGroupAndUsers(group, user)
                .orElseThrow(() -> new UserNotBelongException("User does not belong to this group"));

        List<Vehicle> vehicles = iVehicleRepository.findAllByGroup(group);

        if (vehicles.isEmpty()) {
            throw new NoVehicleInGroupException("No vehicles found in this group");
        }

        // Convert sang List<VehicleRes>
        return vehicles.stream()
                .map(vehicle -> {
                    VehicleRes dto = modelMapper.map(vehicle, VehicleRes.class);
                    dto.setGroupId(group.getGroupId());
                    dto.setGroupName(group.getGroupName());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public void updateSchedule(ScheduleReq req, int scheduleId) {
        // Find existing schedule
        Schedule schedule = iScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        validateTimeRange(req.getStartTime(), req.getEndTime());
        // Validate user exists
        Users user = iUserRepository.findById(req.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate group exists
        Group group = iGroupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        // Validate user belongs to group
        GroupMember gm = iGroupMemberRepository.findByGroupAndUsers(group, user)
                .orElseThrow(() -> new UserNotBelongException("User does not belong to this group"));

        // Validate vehicle belongs to group
        Vehicle vehicle = iVehicleRepository.findVehicleByVehicleId(req.getVehicleId());
        if (vehicle == null || vehicle.getGroup().getGroupId() != req.getGroupId()) {
            throw new VehicleNotBelongException("Vehicle does not belong to this group");
        }
        checkTimeConflicts(req.getGroupId(), req.getStartTime(), req.getEndTime(), gm, user, scheduleId);
        // Update schedule fields
        schedule.setStartTime(req.getStartTime());
        schedule.setEndTime(req.getEndTime());
        schedule.setGroupMember(gm);
        // Save updated schedule
        iScheduleRepository.save(schedule);
    }

    @Override
    public void cancelSchedule(int scheduleId) {
        Schedule schedule = iScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // Đổi status thay vì delete
        schedule.setStatus(StatusSchedule.CANCELED);
        iScheduleRepository.save(schedule);
    }

    @Override
    public List<ScheduleRes> findByGroupMember_Group_GroupId(int groupId) {
        Group group = iGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        List<Schedule> schedules = iScheduleRepository.findByGroupMember_Group_GroupId(groupId);

        return schedules.stream()
                .filter(s -> !s.getStatus().equals(StatusSchedule.OVERRIDE_TRACKER))
                .map(schedule -> {
                    ScheduleRes res = modelMapper.map(schedule, ScheduleRes.class);
                    res.setUserId(schedule.getGroupMember().getUsers().getId());
                    res.setUserName(schedule.getGroupMember().getUsers().getUsername());
                    res.setGroupId(schedule.getGroupMember().getGroup().getGroupId());
                    res.setOwnershipPercentage(schedule.getGroupMember().getOwnershipPercentage());
                    Vehicle vehicle = iVehicleRepository.findByGroup(schedule.getGroupMember().getGroup());
                    if (vehicle != null) {
                        res.setVehicleId(vehicle.getVehicleId());
                    }

                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    public OverrideInfoRes getOverrideCountForUser(int userId, int groupId) {
        Users user = iUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Group group = iGroupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        GroupMember gm = iGroupMemberRepository.findByGroupAndUsers(group, user)
                .orElseThrow(() -> new UserNotBelongException("User does not belong to this group"));

        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        LocalDateTime endOfMonth = LocalDateTime.now()
                .withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59);

        long overrideCount = iScheduleRepository
                .countByGroupMember_IdAndStatusAndCreatedAtBetween(
                        gm.getId(),
                        StatusSchedule.OVERRIDE_TRACKER,
                        startOfMonth,
                        endOfMonth
                );
        return new OverrideInfoRes(
                userId,
                groupId,
                overrideCount,
                2 - overrideCount,
                2,
                startOfMonth.getMonth().toString(),
                startOfMonth.plusMonths(1).toLocalDate());
    }

    private void sendSimpleOverrideEmail(Users affectedUser, Users overridingUser, Schedule canceledSchedule) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Tạo Thymeleaf Context và set variables
            Context context = new Context();
            context.setVariable("fullName", affectedUser.getHovaTen());
            context.setVariable("startTime", canceledSchedule.getStartTime().format(formatter));
            context.setVariable("endTime", canceledSchedule.getEndTime().format(formatter));
            context.setVariable("overridingUserEmail", overridingUser.getUsername());

            // Process template với Thymeleaf
            String htmlContent = templateEngine.process("scheduleOverrideNotification", context);

            // Tạo EmailDetailReq
            EmailDetailReq contentSender = new EmailDetailReq();
            contentSender.setEmail(affectedUser.getEmail());
            contentSender.setSubject("[EcoShare] Thông báo chèn lịch");
            contentSender.setTemplate(htmlContent);

            emailService.sendEmail(contentSender);

        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }


    // --------------------------------------------------------------------------
    // Function to use in create and update schedule
    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isBefore(now)) {
            throw new PastDateBookingException("Cannot book schedule in the past");
        }

        if (endTime.isBefore(now)) {
            throw new PastDateBookingException("End time must be in the future");
        }

        if (endTime.isBefore(startTime)) {
            throw new PastDateBookingException("End time must be after start time");
        }
    }

    private void checkTimeConflicts(int groupId, LocalDateTime startTime, LocalDateTime endTime,
                                    GroupMember gm, Users user, Integer excludeScheduleId) {
        // Find conflicting schedules
        List<Schedule> conflictingSchedules = iScheduleRepository
                .findByGroupMember_Group_GroupId(groupId)
                .stream()
                .filter(s -> !s.getStatus().equals(StatusSchedule.CANCELED) &&
                        !s.getStatus().equals(StatusSchedule.OVERRIDDEN) &&
                        !s.getStatus().equals(StatusSchedule.OVERRIDE_TRACKER))
                .filter(s -> excludeScheduleId == null || s.getScheduleId() != excludeScheduleId) // Exclude current schedule if updating
                .filter(s -> s.getStartTime().isBefore(endTime) && s.getEndTime().isAfter(startTime))
                .toList();
        if (conflictingSchedules.size() > 1) {
            String timeSlots = conflictingSchedules.stream()
                    .map(s -> String.format("%s - %s (by %s)",
                            s.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            s.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            s.getGroupMember().getUsers().getUsername()))
                    .collect(Collectors.joining(", "));
            throw new TimeConflictException(
                    String.format("Cannot book schedule. Overlaps with %d existing bookings. Please select another time.",
                            conflictingSchedules.size())
            );
        }

        // Handle conflicts with ownership priority
        if (!conflictingSchedules.isEmpty()) {
            // Check override limit
            checkOverrideLimit(gm);
            LocalDateTime now = LocalDateTime.now();

            // Process each conflicting schedule
            for (Schedule conflictSchedule : conflictingSchedules) {
                LocalDateTime scheduleStartTime = conflictSchedule.getStartTime();
                long hoursUntilStart = java.time.Duration.between(now, scheduleStartTime).toHours();

                // Cannot override within 24 hours
                if (hoursUntilStart < 24) {
                    throw new OverrideNotAllowedException(
                            "Cannot override schedule starting within 24 hours"
                    );
                }

                float existingOwnership = conflictSchedule.getGroupMember().getOwnershipPercentage();
                float newOwnership = gm.getOwnershipPercentage();

                if (newOwnership > existingOwnership) {
                    // Higher ownership -> override existing schedule
                    overrideSchedule(conflictSchedule, gm, user);
                } else if (newOwnership == existingOwnership) {
                    throw new LowerOwnershipException(
                            "Cannot override schedule. Equal ownership percentage - first come first served principle applies"
                    );
                } else {
                    throw new LowerOwnershipException(
                            String.format(
                                    "Cannot override schedule. Your ownership (%.1f%%) is lower than existing booking (%.1f%%)",
                                    newOwnership,
                                    existingOwnership
                            )
                    );
                }
            }
        }
    }

    private void checkOverrideLimit(GroupMember gm) {
        LocalDateTime startOfMonth = getStartOfMonth();
        LocalDateTime endOfMonth = getEndOfMonth();

        long overrideCount = iScheduleRepository
                .countByGroupMember_IdAndStatusAndCreatedAtBetween(
                        gm.getId(),
                        StatusSchedule.OVERRIDE_TRACKER,
                        startOfMonth,
                        endOfMonth
                );

        if (overrideCount >= 2) {
            throw new OverrideLimitExceededException(
                    "Override limit exceeded. You have used all 2 overrides this month. " +
                            "Next reset: " + startOfMonth.plusMonths(1).toLocalDate()
            );
        }
    }

    private LocalDateTime getStartOfMonth() {
        return LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0);
    }

    private LocalDateTime getEndOfMonth() {
        return LocalDateTime.now()
                .withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59);
    }

    private void overrideSchedule(Schedule conflictSchedule, GroupMember overridingGm, Users overridingUser) {
        // Mark existing schedule as overridden
        conflictSchedule.setStatus(StatusSchedule.OVERRIDDEN);
        iScheduleRepository.save(conflictSchedule);

        // Create override tracker
        Schedule tracker = new Schedule();
        tracker.setGroupMember(overridingGm);
        tracker.setStatus(StatusSchedule.OVERRIDE_TRACKER);
        tracker.setStartTime(LocalDateTime.now());
        tracker.setEndTime(LocalDateTime.now());
        tracker.setCreatedAt(LocalDateTime.now());
        iScheduleRepository.save(tracker);

        // Send notification email
        Users affectedUser = conflictSchedule.getGroupMember().getUsers();
        sendSimpleOverrideEmail(affectedUser, overridingUser, conflictSchedule);

        System.out.println(String.format(
                "Schedule overridden: User %s (%.1f%% ownership) overrode User %s (%.1f%% ownership)",
                overridingUser.getUsername(),
                overridingGm.getOwnershipPercentage(),
                affectedUser.getUsername(),
                conflictSchedule.getGroupMember().getOwnershipPercentage()
        ));
    }

    // automatic reset override
    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyOverrideTrackers() {
        LocalDateTime startOfCurrentMonth = getStartOfMonth();

        List<Schedule> oldTrackers = iScheduleRepository.findByStatusAndCreatedAtBefore(
                StatusSchedule.OVERRIDE_TRACKER,
                startOfCurrentMonth
        );

        if (!oldTrackers.isEmpty()) {
            iScheduleRepository.deleteAll(oldTrackers);
            System.out.println(String.format(
                    "Monthly reset: Đã xóa %d bản ghi override tracker cũ (trước %s)",
                    oldTrackers.size(),
                    startOfCurrentMonth.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            ));
        }

    }
}
