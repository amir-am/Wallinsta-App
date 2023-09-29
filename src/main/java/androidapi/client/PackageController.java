package androidapi.client;

import androidapi.database.DbConnector;
import androidapi.model.main.Message;
import androidapi.model.main.Response;
import androidapi.model.packages.OrderPackage;
import androidapi.model.packages.PackageList;
import androidapi.model.packages.PackageType;
import androidapi.model.packages.SalePackage;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Amir Hossein on 7/30/2017.
 */
@RestController
@RequestMapping("/package_controller")
public class PackageController {

    @RequestMapping(value = "/add_orderitem", method = RequestMethod.POST)
    @ResponseBody
    public Response addOrderItem(@RequestBody OrderPackage orderPackage) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            session.saveOrUpdate(orderPackage);
            tx.commit();
            return (new Response(orderPackage));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.ADD_ORDER_ITEM_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/get_orderpack", method = RequestMethod.GET)
    @ResponseBody
    public Response getOrderPackage() {
        PackageList packageList = new PackageList();
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work;
            String sql = "FROM androidapi.model.packages.OrderPackage P WHERE P.type =:like and P.active=:trueP ";
            Query query = session.createQuery(sql);
            query.setParameter("like", PackageType.LIKE);
            query.setParameter("trueP", true);
            packageList.setLikeList(query.list());

            sql = "FROM androidapi.model.packages.OrderPackage P WHERE P.type =:comment and P.active=:trueP";
            query = session.createQuery(sql);
            query.setParameter("comment", PackageType.COMMENT);
            query.setParameter("trueP", true);
            packageList.setCommentList(query.list());

            sql = "FROM androidapi.model.packages.OrderPackage P WHERE P.type =:follower and P.active=:trueP";
            query = session.createQuery(sql);
            query.setParameter("follower", PackageType.FOLLOW);
            query.setParameter("trueP", true);
            packageList.setFollowerList(query.list());

            tx.commit();
            return (new Response<PackageList>(packageList));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_ORDER_PACKAGE_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/remove_orderitem", method = RequestMethod.POST)
    @ResponseBody
    public Response removeOrderItem(@RequestBody int id) {
        OrderPackage newOrderItem = new OrderPackage();
        newOrderItem.setId(id);
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            session.delete(newOrderItem);
            tx.commit();
            return (new Response(newOrderItem));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.REMOVE_ORDER_ITEM_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/add_saleitem", method = RequestMethod.POST)
    @ResponseBody
    public Response addSaleItem(@RequestBody SalePackage salePackage) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            session.saveOrUpdate(salePackage);
            tx.commit();
            return (new Response(salePackage));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.ADD_SALE_ITEM_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/get_salepack", method = RequestMethod.GET)
    @ResponseBody
    public Response getSalePackage() {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work;
            String sql = "FROM androidapi.model.packages.SalePackage SP where SP.active =:param ORDER BY SP.price";
            Query query = session.createQuery(sql);
            query.setParameter("param", true);
            tx.commit();
            return (new Response(query.list()));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_SALE_PACKAGE_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/remove_saleitem", method = RequestMethod.POST)
    @ResponseBody
    public Response removeSaleItem(@RequestBody int id) {
        SalePackage newSaleItem = new SalePackage();
        newSaleItem.setId(id);
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            session.delete(newSaleItem);
            tx.commit();
            return (new Response(newSaleItem));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.REMOVE_SALE_ITEM_EXCEPTION));
        } finally {
            session.close();
        }
    }
}
