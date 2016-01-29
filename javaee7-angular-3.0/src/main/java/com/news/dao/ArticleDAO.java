package com.news.dao;

import com.news.entities.Article;
import com.news.entities.User;

import javax.ejb.Local;
import java.util.Collection;
import java.util.List;

/**
 * Created by Maksym on 1/12/2016.
 */
@Local
public interface ArticleDAO {

    Article saveArticle(Long pId,Article article);

    void deleteArticle(Long personId, Article article);

    Article findArticle(Long aId);

    List<Article> getAll();

    List<Article> findArticlesForDate(String date);

    List<Article> findArticlesForCreator(Long crtId);

    Article changeArticlesCreator(Long pId, Long articleId, Long crtrId);
}
