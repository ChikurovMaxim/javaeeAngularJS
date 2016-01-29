package com.news.rest;

import com.news.dao.RoleDAO;
import com.news.entities.Role;
import com.news.pagination.PaginatedListWrapper;
import com.news.dao.UserDAO;
import com.news.entities.User;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * REST Service to expose the data to display in the UI grid.
 *
 * @author Roberto Cortez
 */
@ApplicationPath("/resources")
@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends Application {

    @EJB(name = "java:global/UserDAOImpl")
    UserDAO userDAO;

    @EJB(name = "java:global/RoleDAOImpl")
    RoleDAO roleDAO;

    private PaginatedListWrapper<User> findPersons(PaginatedListWrapper<User> wrapper) {
        wrapper.setTotalResults(userDAO.countPersons());
        int start = (wrapper.getCurrentPage() - 1) * wrapper.getPageSize();
        wrapper.setList(userDAO.findPersons(start,
                wrapper.getPageSize(),
                wrapper.getSortFields(),
                wrapper.getSortDirections()));
        return wrapper;
    }

    private PaginatedListWrapper<Role> findRoles(PaginatedListWrapper<Role> wrapper) {
        wrapper.setTotalResults(userDAO.countPersons());
        wrapper.setList(userDAO.getAllPersonsRoles(wrapper.getUserId()));
        return wrapper;
    }

    @Transactional
    public User saveRolesForPerson(Long pId,Long personId, HashSet<Role> roles){
        User p = userDAO.findPerson(personId);
        List<Role> roleList = processRoles(roles);
        p.setRole(roleList);
        return userDAO.savePerson(pId,p);
    }

    private List<Role> processRoles(HashSet<Role> roles){
        List<Role> newRolesList = new ArrayList<>();
        for(Role role: roles){
            Role newR = roleDAO.findRole(role.getId());
            newRolesList.add(newR);
        }
        return newRolesList;
    }

    private User validateLoginData(String login, String password){
        return userDAO.getUserIdByAuthData(login, password);
    }

    @POST
    @Path("/login/{login}")
    @Consumes(MediaType.APPLICATION_JSON)
    public User login(@PathParam("login") String login, String password,@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        User logedP = validateLoginData(login,password);
        session.setAttribute("personId", logedP.getId());
        return logedP;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("roles")
    public PaginatedListWrapper<Role> listRoles(@DefaultValue("1")
                                                @QueryParam("id")
                                                    Long id) {
        PaginatedListWrapper<Role> paginatedListWrapper = new PaginatedListWrapper<>();
        paginatedListWrapper.setUserId(id);
        return findRoles(paginatedListWrapper);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public PaginatedListWrapper<User> listPersons(@DefaultValue("1")
                                                    @QueryParam("page")
                                                        Integer page,
                                                    @DefaultValue("id")
                                                    @QueryParam("sortFields")
                                                        String sortFields,
                                                    @DefaultValue("asc")
                                                    @QueryParam("sortDirections")
                                                        String sortDirections) {
        PaginatedListWrapper<User> paginatedListWrapper = new PaginatedListWrapper<>();
        paginatedListWrapper.setCurrentPage(page);
        paginatedListWrapper.setSortFields(sortFields);
        paginatedListWrapper.setSortDirections(sortDirections);
        paginatedListWrapper.setPageSize(10);
        return findPersons(paginatedListWrapper);
    }

    @GET
    @Path("/logout")
    public String logOut(@Context HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("personId", null);
        session.invalidate();
        return "Bye =3";
    }


    @GET
    @Path("{id}")
    public User getPerson( @PathParam("id") Long id) {
        return userDAO.findPerson(id);
    }

    @GET
    @Path("logedIn")
    public User getLogedP( @Context HttpServletRequest request){
        HttpSession session = request.getSession();
        return userDAO.findPerson((Long)session.getAttribute("personId"));
    }

    @GET
    @Path("roles/{id}")
    public List<Role> getPersonsRoles( @PathParam("id") Long id){
        return userDAO.getAllPersonsRoles(id);
    }


    @GET
    @Path("roles/list")
    public List<Role> getAllRoles(){
        return roleDAO.getAll();
    }

    @POST
    @Path("/isAdmin/")
    public boolean isAdmin(String name){
        User u = userDAO.findUserByName(name);
        for(Role r : u.getRole()){
            if(r.getName().equals("ADMIN"))return true;
        }
        return false;
    }

    @POST
    public User savePerson(User person,@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        return userDAO.savePerson((Long)session.getAttribute("personId"),person);
    }

    @POST
    @Path("save/roles/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public User saveRolesForPerson(@PathParam("id") Long personId, Role[] roles,@Context HttpServletRequest request){
        HttpSession session = request.getSession();
        HashSet<Role> rolesl = new HashSet<>();
        Collections.addAll(rolesl, roles);
        return saveRolesForPerson((Long)session.getAttribute("personId"),personId, rolesl);
    }

    @DELETE
    @Path("{id}")
    public void deletePerson(@PathParam("id") Long id, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        User p = userDAO.findPerson(id);
        userDAO.deletePerson((Long) session.getAttribute("personId"), p);
    }
}
