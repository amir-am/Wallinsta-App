package androidapi.client;

import androidapi.database.DbConnector;
import androidapi.model.main.Message;
import androidapi.model.main.Response;
import androidapi.model.packages.SalePackage;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.web.bind.annotation.RestController
@RequestMapping({"/statistics_controller"})
public class StatisticsController {

    @RequestMapping(value = {"/all_purchases"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Response allPurchases() {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            String sql = "from androidapi.model.packages.SalePackage";
            Query query = session.createQuery(sql);
            SalePackage[] salePackages = new SalePackage[query.list().size()];
            for (int i = 0; i < query.list().size(); i++) {
                salePackages[i] = ((SalePackage) query.list().get(i));
            }

            String sql2 = "from androidapi.model.logutil.PurchaseLog";
            Query query2 = session.createQuery(sql2);
            int sum = 0;
            for (int i = 0; i < query2.list().size(); i++) {
                androidapi.model.logutil.PurchaseLog purchaseLog = (androidapi.model.logutil.PurchaseLog) query2.list().get(i);
                for (int j = 0; j < salePackages.length; j++) {
                    if (purchaseLog.getProductId().equals(salePackages[j].getSku())) {
                        sum += salePackages[j].getPrice();
                    }
                }
            }
            return new Response(Integer.valueOf(sum), "Total past purchases");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.ALL_PURCHASES_CALCULATE_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = {"/like_average"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Response likeAverage() {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Calendar c = Calendar.getInstance();
            c.add(7, -7);
            String sql = "select count (UL.id) from androidapi.model.userdid.UserLiked UL where UL.liked =:param and UL.likeDate > :param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", Boolean.valueOf(true));
            query.setParameter("param2", c.getTime());

            return new Response(Long.valueOf(((Long) query.list().get(0)).longValue() / 7L), "Average weekly like");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.LIKE_AVERAGE_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = {"/comment_average"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Response commentAverage() {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Calendar c = Calendar.getInstance();
            c.add(7, -7);
            String sql = "select count (UC.id) from androidapi.model.userdid.UserCommented UC where UC.commented =:param and UC.commentDate > :param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", Boolean.valueOf(true));
            query.setParameter("param2", c.getTime());

            return new Response(Long.valueOf(((Long) query.list().get(0)).longValue() / 7L), "Average weekly comment");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.COMMENT_AVERAGE_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = {"/follow_average"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Response followAverage() {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Calendar c = Calendar.getInstance();
            c.add(7, -7);
            String sql = "select count (UF.id) from androidapi.model.userdid.UserFollowed UF where UF.followed =:param and UF.followDate > :param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", Boolean.valueOf(true));
            query.setParameter("param2", c.getTime());

            return new Response(Long.valueOf(((Long) query.list().get(0)).longValue() / 7L), "Average weekly follow");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.FOLLOW_AVERAGE_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }
}
