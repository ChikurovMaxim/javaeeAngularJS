package com.news.daoimpl;

import com.news.dao.RoleDAO;
import com.news.entities.Role;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Maksym on 1/12/2016.
 */
@Stateless
@EJB(name = "java:global/RoleDAOImpl",
        beanInterface = RoleDAO.class, beanName = "RoleDAOImpl")
public class RoleDAOImpl implements RoleDAO {

    @PersistenceContext(unitName = "UNIT2")
    private EntityManager entityManager;


    @Override
    @Transactional
    public void addRole(Role role) {
        entityManager.persist(role);
    }

    @Override
    @Transactional
    public void deleteRole(Role role) {
        entityManager.remove(role);
    }

    @Override
    public List<Role> getAll() {
        Query q = entityManager.createQuery("select r From Role r");
        return q.getResultList();
    }
    @Override
    public Role findRole(Long id){
        Query query = entityManager.createQuery("select r from Role r where r.id="+id );
        return (Role)query.getSingleResult();
    }

    @Override
    public Integer countRoles() {
        Query query = entityManager.createQuery("SELECT COUNT(r.id) FROM Role r");
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Role> findRoles(int startPosition, int maxResults, String sortFields, String sortDirections) {
        Query query = entityManager.createQuery("SELECT r FROM Role r ORDER BY " + sortFields);
        query.setFirstResult(startPosition);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }
}
