package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.pojo.UserRole;

public interface IUserRoleService {
    public UserRole addUserRole(UserRole userRole);

    public UserRole findByRoleName(String rolename);

    public  boolean existsByRoleName(String rolename);

    public UserRole findUserRoleByRoleId(int id);
}
