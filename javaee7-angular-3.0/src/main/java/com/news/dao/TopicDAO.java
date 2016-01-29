package com.news.dao;

import com.news.entities.Topic;

import javax.ejb.Local;
import java.util.Collection;
import java.util.List;

/**
 * Created by Maksym on 1/12/2016.
 */
@Local
public interface TopicDAO {

    Topic saveTopic(Long pId, Topic t);

    void deleteTopic(Long personId, Topic t);

    Topic findTopic(Long topicId);

    List<Topic> getAll();
}
