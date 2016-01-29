package dbtests;

import com.news.dao.RoleDAO;
import com.news.dao.UserDAO;
import com.news.entities.Role;
import com.news.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * Created by Maksym on 1/21/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-context.xml")
public class UsersTest {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RoleDAO roleDAO;

    User u = null;

    @Before
    public void beforTest(){
        u = new User("name",roleDAO.getAll(),null,null);
        userDAO.savePerson(2L, u);
    }

    @Test
    public void testSaveUser(){
        assertNotNull(u);
        assertNotNull(userDAO.findUserByName("name").getId());
    }

    @Test
    public void testEdit(){
        u.setName("name2");
        userDAO.savePerson(2L,  u);
        assertEquals("name2", userDAO.findUserByName(u.getName()).getName());
    }

    @Test
    public void testPersonsRoles(){
        List<Role> firstLook, secondLook;
        firstLook = (List<Role>) u.getRole();
        for(Role r : firstLook){
            if(r.getName().equals("ADMIN"))assertTrue(true);
        }
        firstLook.remove(1);
        u.setRole(firstLook);
        secondLook = (List<Role>)u.getRole();
        for(Role r : secondLook){
            if(!r.getName().equals("ADMIN"))assertTrue(true);
        }
    }
    @Test
    public void testFindUserByRole(){
        List<User> users =  userDAO.findUsersByRole(roleDAO.findRole(1l));
        for(User u : users) {
            for (Role r : u.getRole()) {
                if (r.getName().equals("ADMIN")) {
                    assertTrue(true);
                }
            }
        }
    }

    @Test
    public void testFindUserByName(){
        String name = "Uzumaki Naruto";
        User u = userDAO.findUserByName(name);
        if(u.getName().equals(name))assertTrue(true);
    }

    @Test
    public void testLoginData(){
        assertNull(userDAO.getUserIdByAuthData("asd","asd"));
        assertNotNull(userDAO.getUserIdByAuthData("naruto","1q2w3e"));
    }

    @Test
    public void testRemove(){
        int firstLook, secondLook;
        firstLook = userDAO.getAll().size();
        if(userDAO.findUserByName("name2")!=null  )
            userDAO.deletePerson(2L, userDAO.findUserByName("name2"));
        else if(userDAO.findUserByName("name")!=null)
            userDAO.deletePerson(2L, userDAO.findUserByName("name"));
        secondLook = userDAO.getAll().size();
        assertNotEquals(firstLook,secondLook);
    }

}
