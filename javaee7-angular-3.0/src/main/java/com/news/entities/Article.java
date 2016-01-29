package com.news.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Maksym on 1/12/2016.
 */
@Entity
@Table(name = "article")
public class Article implements Serializable{
    public Article(){}
    public Article(String context, String content, User creator, Topic topic, String creation_time) {
        this.context = context;
        this.content = content;
        this.creator = creator;
        this.topic = topic;
        this.date = creation_time;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String context;

    String content;

    @OneToOne(targetEntity = User.class,cascade = CascadeType.ALL)
    @JoinColumn(name="creator_id", referencedColumnName = "ID")
    User creator;

    @ManyToOne
    @JoinColumn(name = "topic_id",referencedColumnName = "ID" )
    Topic topic;

    String date;


    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String creation_date) {
        this.date = creation_date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (id != null ? !id.equals(article.id) : article.id != null) return false;
        if (context != null ? !context.equals(article.context) : article.context != null) return false;
        if (content != null ? !content.equals(article.content) : article.content != null) return false;
        if (creator != null ? !creator.equals(article.creator) : article.creator != null) return false;
        if (topic != null ? !topic.equals(article.topic) : article.topic != null) return false;
        return !(date != null ? !date.equals(article.date) : article.date != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
