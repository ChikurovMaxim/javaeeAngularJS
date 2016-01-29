package com.news.daoimpl;

import com.news.dao.ArticleDAO;
import com.news.entities.Article;
import com.news.entities.Role;
import com.news.entities.User;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

/**
 * Created by Maksym on 1/12/2016.
 */
@Stateless
@EJB(name = "java:global/ArticleDAOImpl",
        beanInterface = ArticleDAO.class, beanName = "ArticleDAOImpl")
public class ArticleDAOImpl implements ArticleDAO{


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
    public Article saveArticle(Long personId ,Article newNews) {
        Article newsOne = new Article();
        if(newNews.getId()!=null)
            newsOne = newNews;
        else {
            newsOne.setContext(newNews.getContext());
            newsOne.setContent(newNews.getContent());
        }
        User p = entityManager.find(User.class, personId);
        newsOne.setCreator(p);
        boolean isAdmin = false;
        boolean isModer = false;
        for(Role r : newsOne.getCreator().getRole()){
            if(Objects.equals(r.getName(), "ADMIN"))
                isAdmin = true;
            else if(Objects.equals(r.getName(), "MODERATOR"))
                isModer = true;
        }
        if( isAdmin && newsOne.getId() == null)entityManager.persist(newsOne);
        else if(isAdmin) entityManager.merge(newsOne);
        else if(isModer && newsOne.getId() != null)entityManager.merge(newsOne);
        return newsOne;
    }

    @Override
    @Transactional
    public void deleteArticle(Long personId,Article news) {
        User u = entityManager.find(User.class, personId);
        for(Role r : u.getRole()){
            if(Objects.equals(r.getName(), "ADMIN")){
                entityManager.remove(entityManager.contains(news) ? news : entityManager.merge(news));
            }
        }

    }

    @Override
    public List<Article> getAll() {
        Query q = entityManager.createQuery("SELECT n FROM Article n");
        return q.getResultList();
    }

    @Override
    public Article findArticle(Long id) {
        Query q = entityManager.createQuery("SELECT n FROM Article n WHERE n.id = :artId");
        q.setParameter("artId", id);
        return (Article)q.getSingleResult();
    }

    @Override
    public List<Article> findArticlesForDate(String date){
        Query q = entityManager.createQuery("SELECT a FROM Article a WHERE a.date = :artDate");
        q.setParameter("artDate",date);
        return q.getResultList();
    }

    @Override
    public List<Article> findArticlesForCreator(Long crtId){
        User u  = entityManager.find(User.class, crtId);
        Query q = entityManager.createQuery("SELECT a FROM Article a WHERE a.creator = :crtr");
        q.setParameter("crtr", u);
        return q.getResultList();
    }

    @Transactional
    @Override
    public Article changeArticlesCreator(Long pId, Long articleId, Long crtrId){
        if(isAdmin(pId)){
            User u = entityManager.find(User.class, crtrId);
            Article a = entityManager.find(Article.class, articleId);
            if(u!=null && a!=null){
                a.setCreator(u);
                entityManager.merge(a);
                return a;
            }
            else return null;
        }
        return null;
    }
}
