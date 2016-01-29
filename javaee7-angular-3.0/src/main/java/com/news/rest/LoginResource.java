package com.news.rest;

import com.news.dao.UserDAO;
import com.news.entities.User;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


@ApplicationPath("/resources")
@Path("login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @EJB(name = "java:global/UserDAOImpl")
    UserDAO personService;

    private User validateLoginData(String login, String password){
        return personService.getUserIdByAuthData(login,password);
    }

    @POST
    @Path("{login}")
    @Consumes(MediaType.APPLICATION_JSON)
    public User login(@PathParam("login") String login, String password,@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        User logedP = validateLoginData(login,password);
        session.setAttribute("personId", logedP.getId());
        return logedP;
    }

}
