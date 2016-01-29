package com.news.dao;

import com.news.entities.Role;

import javax.ejb.Local;
import java.util.List;

/**
 * Created by Maksym on 1/12/2016.
 */
@Local
public interface RoleDAO {

    void addRole(Role role);

    void deleteRole(Role role);

    List<Role> getAll();

    Role findRole(Long id);

    Integer countRoles();

    @SuppressWarnings("unchecked")
    List<Role> findRoles(int startPosition, int maxResults, String sortFields, String sortDirections);
}
