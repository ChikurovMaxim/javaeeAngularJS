package com.news.rest;


import com.news.dao.ArticleDAO;
import com.news.dao.TopicDAO;
import com.news.dao.UserDAO;
import com.news.entities.Article;
import com.news.entities.Role;
import com.news.entities.User;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;


@ApplicationPath("/resources")
@Path("article")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @EJB(name = "java:global/ArticleDAOImpl")
    ArticleDAO articleDAO;

    @EJB(name = "java:global/UserDAOImpl")
    UserDAO userDAO;

    @EJB(name = "java:global/TopicDAOImpl")
    TopicDAO topicDAO;

    private User validateLoginData(String login, String password){
        return userDAO.getUserIdByAuthData(login,password);
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
    @Path("/login/{login}")
    @Consumes(MediaType.APPLICATION_JSON)
    public User login(@PathParam("login") String login, String password,@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        User logedP = validateLoginData(login,password);
        session.setAttribute("personId", logedP.getId());
        return logedP;
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
    @Path("list")
    public List<Article> getAllArticles(){
        return articleDAO.getAll();
    }

    @GET
    @Path("news/{id}")
    public Article getOne(@PathParam("id") Long Id){
        return articleDAO.findArticle(Id);
    }

    @GET
    @Path("logedIn")
    public User getLogedP( @Context HttpServletRequest request){
        HttpSession session = request.getSession();
        return userDAO.findPerson((Long) session.getAttribute("personId"));
    }

    @DELETE
    @Path("remove/{id}")
    public void deleteArticle(@PathParam("id")Long id,@Context HttpServletRequest request){
        HttpSession session = request.getSession();
        Article n = articleDAO.findArticle(id);
        articleDAO.deleteArticle((Long)session.getAttribute("personId"), n);
    }

    @POST
    @Path("save/{id}/{topicId}")
    public Article saveArticle(@PathParam("id")Long id,@PathParam("topicId") Long topicId,String context, String content,
                            String creationDate, String editionDate, @Context HttpServletRequest request){
        HttpSession session = request.getSession();
        Article article = new Article();
        if(id!=null)article = articleDAO.findArticle(id);
        article.setTopic(topicDAO.findTopic(topicId));
        article.setContext(context);
        article.setContent(content);
        article.setDate(creationDate);
        articleDAO.saveArticle((Long) session.getAttribute("personId"), article);
        return article;
    }

    /**
     * Function for just created Article
     */
    @POST
    @Path("save/")
    public Article saveArticle( Long topicId, String context, String newsContent,
                             String creationDate, @Context HttpServletRequest request){
        return saveArticle(null,topicId, context, newsContent,creationDate,null, request);
    }

    /**
     * Function for just edited Article
     */
    @POST
    @Path("save/")
    public Article saveArticle( Long id, Long topicId, String context, String newsContent,
                                String editedDate, @Context HttpServletRequest request){
        return saveArticle(id, topicId, context, newsContent,null, editedDate, request);
    }
}
