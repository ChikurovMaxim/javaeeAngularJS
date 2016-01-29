package com.news.rest;

import com.news.dao.RoleDAO;
import com.news.dao.TopicDAO;
import com.news.dao.UserDAO;
import com.news.entities.Topic;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Maksym on 1/13/2016.
 */
@ApplicationPath("/resources")
@Path("topic")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TopicResource {
    @EJB(name = "java:global/TopicDAOImpl")
    TopicDAO topicDAO;

    @EJB(name = "java:global/RoleDAOImpl")
    RoleDAO roleDAO;

    @GET
    @Path("getAll")
    public List<Topic> getAllTopics(){return topicDAO.getAll();}

    @GET
    @Path("getTopic/{id}")
    public Topic getTopic(@PathParam("id")Long topicId){
        return topicDAO.findTopic(topicId);
    }

    @POST
    @Path("save/{topicId}/{topicName}")
    public Topic saveNews(@PathParam("topicId")Long id,@PathParam("topicName")String topicName,String description, @Context HttpServletRequest request){
        HttpSession session = request.getSession();
        Topic topic = new Topic();
        if(id!=null)topic = topicDAO.findTopic(id);
        topic.setName(topicName);
        topic.setDescription(description);
        topicDAO.saveTopic((Long) session.getAttribute("personId"), topic);
        return topic;
    }

    @POST
    @Path("save/")
    public Topic saveNews(  String topicName, String description, @Context HttpServletRequest request){
        return saveNews(null,topicName, description, request);
    }

    @DELETE
    @Path("delete/{topicId}")
    public void removeTopic(@PathParam("topicId") Long topicId,@Context HttpServletRequest request){
        HttpSession session = request.getSession();
        topicDAO.deleteTopic((Long)session.getAttribute("personId"),topicDAO.findTopic(topicId));
    }
}
