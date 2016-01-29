package com.news.daoimpl;

import com.news.dao.UserDAO;
import com.news.entities.Article;
import com.news.entities.Role;
import com.news.entities.User;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Maksym on 1/12/2016.
 */
@Stateless
@EJB(name = "java:global/UserDAOImpl",
        beanInterface = UserDAO.class, beanName = "UserDAOImpl")
public class UserDAOImpl implements UserDAO {


    @PersistenceContext(unitName = "UNIT2")
    private EntityManager entityManager;

    private boolean isAdmin(Long pId){
        Query q = entityManager.createQuery("SELECT p FROM User p WHERE p.id=:pId");
        q.setParameter("pId",pId);
        User p = (User)q.getSingleResult();
        for(Role r :p.getRole()){
            if(Objects.equals(r.getName(), "ADMIN")){
                return true;
            }
        }
        return false;
    }
    @Override
    @Transactional
    public User savePerson(Long pid, User newPerson) {
        List<Role> roles = new ArrayList<>();
        for(Role r :  newPerson.getRole()) {
            Role r1 = entityManager.find(Role.class,r.getId() );
            roles.add(r1);
        }
        User u = new User(newPerson.getName(),roles,newPerson.getLogin(),newPerson.getPassword());
        if(isAdmin(pid)) {
            if (newPerson.getId() == null) {
                entityManager.persist(u);
            } else {
                u.setId(newPerson.getId());
                entityManager.merge(u);
            }
        }
        return u;
    }

    @Override
    public User findPerson(Long id){
        if(id != null)return entityManager.find(User.class,id);
        else return null;
    }

    @Override
    public User findUserByName(String name) {
        Query q = entityManager.createQuery("SELECT u FROM User u WHERE u.name = :uname");
        q.setParameter("uname", name);
        return (User)q.getSingleResult();
    }

    @Override
    public List<User> findUsersByRole(Role role){
        List<User> users = new ArrayList<>();
        for(User u : getAll()){
            for(Role r : u.getRole()){
                if(r.equals(role)){
                    users.add(u);
                }
            }
        }
        return users;
    }

    @Override
    @Transactional
    public void deletePerson(Long pId,User person) {
        User person4usage = null;
        if(isAdmin(pId))
        {
            Query q = entityManager.createQuery("SELECT n FROM Article n WHERE n.creator.id = :pId");
            q.setParameter("pId", person.getId());
            for (User p : getAll()){
                if(p.getName().equals("UNDEFINED"))person4usage = p;
            }
            for(Article n : (List<Article>)q.getResultList()){
                n.setCreator(person4usage);
            }
            User newP = entityManager.find(User.class, person.getId());
            entityManager.remove(newP);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAll() {
        Query q = entityManager.createQuery("select p from User p");
        return q.getResultList();
    }
    @Override
    public List<Role> getAllPersonsRoles(Long id){
        Query q = entityManager.createQuery("select p.role from User p where p.id = :id ");
        q.setParameter("id",id);
        return q.getResultList();
    }

    @Override
    public Integer countPersons() {
        Query query = entityManager.createQuery("SELECT COUNT(p.id) FROM User p");
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> findPersons(int startPosition, int maxResults, String sortFields, String sortDirections) {
        List<User> pList = new ArrayList<>();
        Query query = entityManager.createQuery("SELECT p FROM User p ORDER BY " + sortFields + " " + sortDirections);
        query.setFirstResult(startPosition);
        query.setMaxResults(maxResults);
        for(User p : (List<User>)query.getResultList()){
            if ( !p.getName().equals("UNDEFINED"))pList.add(p);
        }
        return pList;
    }

    @Override
    public User getUserIdByAuthData(String login, String password) {
        try {
            Query q = entityManager.createQuery("select p from User p where p.login = :login and p.password=:passwd");
            q.setParameter("login", login);
            q.setParameter("passwd", password);
            return (User) q.getSingleResult();
        }
        catch (Exception e){
            return null;
        }
    }
}
