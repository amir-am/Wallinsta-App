package androidapi.client;

import androidapi.database.DbConnector;
import androidapi.model.main.Message;
import androidapi.model.main.News;
import androidapi.model.main.Response;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Amir Hossein on 7/30/2017.
 */
@RestController
@RequestMapping("/news_controller")
public class NewsController {
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public Response insertNews(@RequestBody News news) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            session.saveOrUpdate(news);
            tx.commit();
            return (new Response(news));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.INSERT_NEWS_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/get/{start}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public Response getNews(@PathVariable int start, @PathVariable int end) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work;
            String sql = "FROM androidapi.model.main.News N ORDER BY N.createDate DESC";
            Query query = session.createQuery(sql);
            query.setFirstResult(start);
            query.setMaxResults(end);
            tx.commit();
            return (new Response(query.list()));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_NEWS_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Response deleteNews(@RequestBody int id) {
        News news = new News();
        news.setId(id);
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            session.delete(news);
            tx.commit();
            return (new Response(news));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.DELETE_NEWS_EXCEPTION));
        } finally {
            session.close();
        }
    }
}
