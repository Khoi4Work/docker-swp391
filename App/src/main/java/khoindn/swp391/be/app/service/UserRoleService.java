package khoindn.swp391.be.app.service;

import jakarta.transaction.Transactional;
import khoindn.swp391.be.app.exception.exceptions.RoleNotFoundException;
import khoindn.swp391.be.app.pojo.UserRole;
import khoindn.swp391.be.app.repository.IUserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class UserRoleService implements IUserRoleService {

    @Autowired
    private IUserRoleRepository iUserRoleRepository;

    @Override
    public UserRole addUserRole(UserRole userRole) {
        return iUserRoleRepository.save(userRole);
    }

    @Override
    public UserRole findByRoleName(String rolename) {
        UserRole role = iUserRoleRepository.findByRoleName(rolename);
        if (role == null) {
            throw new RoleNotFoundException("Role '" + rolename + "' not found");
        }
        return role;
    }

    @Override
    public boolean existsByRoleName(String rolename) {
        return iUserRoleRepository.existsByRoleName(rolename);
    }

    @Override
    public UserRole findUserRoleByRoleId(int id) {
        return iUserRoleRepository.findUserRoleByRoleId(id);
    }

}
