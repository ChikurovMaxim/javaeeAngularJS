package dbtests;

import com.news.dao.ArticleDAO;
import com.news.dao.TopicDAO;
import com.news.dao.UserDAO;
import com.news.entities.Article;
import com.news.entities.Topic;
import com.news.entities.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 * Created by Maksym on 1/21/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-context.xml")
public class ArticlesTopicsTest {

    @Autowired
    private TopicDAO topicDAO;

    @Autowired
    private ArticleDAO articleDAO;

    @Autowired
    private UserDAO userDAO;

    private Topic t;
    private Article a;

    @Before
    public void beforeTest(){
        User u = userDAO.findPerson(2l);
        t = new Topic("java","java", null);
        a = new Article("context", "content", u, t ,"");
        articleDAO.saveArticle(2l, a);
    }



    @Test
    public void testSaveArticle(){
        int firstLook, secondLook;
        assertNotNull(topicDAO.saveTopic(2l, t));
        firstLook = articleDAO.getAll().size();
        assertNotNull(articleDAO.saveArticle(2l, a));
        secondLook = articleDAO.getAll().size();
        assertNotEquals(firstLook,secondLook);
    }

    @Test
    public void testFindArticle(){
        assertNotNull(articleDAO.findArticle(2L));
        assertNotNull(articleDAO.findArticlesForCreator(2L));
        assertNotNull(articleDAO.findArticlesForDate("2016-01-26-14-44"));
    }

    @Test
    public void testChangeCreator(){
        articleDAO.changeArticlesCreator(2l,4l,1l);
        assertEquals(articleDAO.findArticle(4l).getCreator().getName(),"UNDEFINED");
    }

    @Test
    public void removeTest(){
        int firstLook, secondLook;

        firstLook = articleDAO.getAll().size();
        Article a = articleDAO.findArticle(2l);
        articleDAO.deleteArticle(2l,a);
        secondLook = articleDAO.getAll().size();
        assertNotEquals(firstLook, secondLook);

        firstLook = topicDAO.getAll().size();
        Topic t = topicDAO.findTopic(1l);
        topicDAO.deleteTopic(2l, t);
        secondLook = topicDAO.getAll().size();
        assertNotEquals(firstLook,secondLook);
    }
}
