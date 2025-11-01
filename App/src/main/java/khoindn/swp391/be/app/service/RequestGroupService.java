package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.exception.exceptions.RequestGroupNotFoundException;
import khoindn.swp391.be.app.exception.exceptions.UndefinedChoiceException;
import khoindn.swp391.be.app.model.Request.UpdateRequestGroup;
import khoindn.swp391.be.app.pojo.Users;
import khoindn.swp391.be.app.pojo._enum.StatusRequestGroup;
import khoindn.swp391.be.app.pojo._enum.StatusRequestGroupDetail;
import khoindn.swp391.be.app.repository.IRequestGroupServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RequestGroupService implements IRequestGroupService {

    @Autowired
    private IRequestGroupServiceRepository iRequestGroupServiceRepository;

    @Override
    public List<khoindn.swp391.be.app.pojo.RequestGroupService> getAllRequestGroup() {
        return iRequestGroupServiceRepository.findAll().stream()
                .filter(requestGroup ->
                        requestGroup.getRequestGroupServiceDetail().getStatus()
                                .equals(StatusRequestGroupDetail.PENDING))
                .toList();
    }

    @Override
    public void updateRequestGroup(UpdateRequestGroup update, Users staff) {
        khoindn.swp391.be.app.pojo.RequestGroupService req = iRequestGroupServiceRepository.findRequestGroupById(update.getIdRequestGroup());
        if (req == null) {
            throw new RequestGroupNotFoundException("RequestGroupService not found");
        }

        if (update.getIdChoice() == 1) {
            req.setStatus(StatusRequestGroup.SOLVED);
            req.getRequestGroupServiceDetail().setStaff(staff);
            req.getRequestGroupServiceDetail().setStatus(StatusRequestGroupDetail.APPROVED);
            req.getRequestGroupServiceDetail().setSolvedAt(LocalDateTime.now());
            iRequestGroupServiceRepository.save(req);
        } else if (update.getIdChoice() == 0) {
            req.setStatus(StatusRequestGroup.DENIED);
            req.getRequestGroupServiceDetail().setStaff(staff);
            req.getRequestGroupServiceDetail().setStatus(StatusRequestGroupDetail.REJECTED);
            req.getRequestGroupServiceDetail().setSolvedAt(LocalDateTime.now());
            iRequestGroupServiceRepository.save(req);
        } else if (update.getIdChoice() == 2) {
            req.setStatus(StatusRequestGroup.PROCESSING);
            req.getRequestGroupServiceDetail().setStaff(staff);
            req.getRequestGroupServiceDetail().setStatus(StatusRequestGroupDetail.PROCESSING);
            req.getRequestGroupServiceDetail().setSolvedAt(LocalDateTime.now());
            iRequestGroupServiceRepository.save(req);
        } else {
            throw new UndefinedChoiceException("Undefined Choice");

        }
    }
}
