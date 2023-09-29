package androidapi.client;

import androidapi.model.logutil.LogType;
import androidapi.model.main.*;
import androidapi.database.DbConnector;
import androidapi.model.instagramapi.InstagramMedia;
import androidapi.model.instagramapi.InstagramPage;
import androidapi.model.instagramapi.View;
import androidapi.model.order.*;
import androidapi.model.packages.OrderPackage;
import androidapi.model.packages.PackageType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Amir Hossein on 7/31/2017.
 */
@RestController
@RequestMapping("/order_controller")
public class OrderController {
    @RequestMapping(value = "/like_order/{userId}/{packId}", method = RequestMethod.POST)
    @ResponseBody
    public Response likeOrder(@PathVariable int userId, @PathVariable int packId, @RequestBody InstagramMedia instagramMedia) {
        System.out.println(instagramMedia);
        boolean checkUser = Tools.checkUserStatus(userId);
        if (checkUser) return (new Response(Message.USER_BLOCK));
        boolean checkMedia = Tools.checkMediaStatus(instagramMedia.getPk());
        if (checkMedia) return (new Response(Message.MEDIA_BLOCK));

        instagramMedia.setTemporaryCaption();
        instagramMedia.setTemporaryFullName();

        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        String sql;
        Query query;
        try {
            tx = session.beginTransaction();
            // do some work
            sql = "from androidapi.model.packages.OrderPackage op where op.id =:param3";
            query = session.createQuery(sql);
            query.setParameter("param3", packId);
            OrderPackage orderPackage = (OrderPackage) query.list().get(0);
            if (orderPackage.getType() != PackageType.LIKE) {
                return (new Response(Message.PACKAGE_TYPE_WRONG));
            }

            sql = "from androidapi.model.order.LikeOrder LO where LO.media.pk =:param1 and LO.status =:param3";
            query = session.createQuery(sql);
            query.setParameter("param1", instagramMedia.getPk());
            query.setParameter("param3", OrderType.RUNNING);

            if (query.list().size() != 0) {
                return (new Response(Message.ANOTHER_LIKE_ORDER_REGISTERED));
            }
            session.saveOrUpdate(instagramMedia);

            sql = "from androidapi.model.main.User U where U.id =:param2";
            query = session.createQuery(sql);
            query.setParameter("param2", userId);
            User user = (User) query.list().get(0);

            if (user.getDiamond() < orderPackage.getDiscountedDiamond()) {
                tx.commit();
                return (new Response(Message.ENOUGH_DIAMOND));
            }

            if (!orderPackage.isActive()) {
                tx.commit();
                return (new Response(Message.PACKAGE_DISABLE));
            }

            LikeOrder likeOrder = new LikeOrder(instagramMedia, orderPackage.getCount(), userId);
            session.saveOrUpdate(likeOrder);
            Tools.updateDiamonds(session, user, user.getDiamond() - orderPackage.getDiscountedDiamond(), LogType.LIKE_ORDER);
            MyTransaction output = new MyTransaction(user.getDiamond(), -orderPackage.getDiscountedDiamond(), LogType.LIKE_ORDER);
            tx.commit();
            return (new Response<MyTransaction>(output));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.LIKE_ORDER_REGISTRATION_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/comment_order/{userId}/{packId}", method = RequestMethod.POST)
    @ResponseBody
    public Response commentOrder(@PathVariable int userId, @PathVariable int packId, @RequestBody InstagramMedia instagramMedia) {
        System.out.println(instagramMedia);
        boolean checkUser = Tools.checkUserStatus(userId);
        if (checkUser) return (new Response(Message.USER_BLOCK));
        boolean checkMedia = Tools.checkMediaStatus(instagramMedia.getPk());
        if (checkMedia) return (new Response(Message.MEDIA_BLOCK));

        instagramMedia.setTemporaryCaption();
        instagramMedia.setTemporaryFullName();

        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        String sql;
        Query query;
        try {
            tx = session.beginTransaction();
            // do some work
            sql = "from androidapi.model.packages.OrderPackage op where op.id =:param3";
            query = session.createQuery(sql);
            query.setParameter("param3", packId);
            OrderPackage orderPackage = (OrderPackage) query.list().get(0);
            if (orderPackage.getType() != PackageType.COMMENT) {
                return (new Response(Message.PACKAGE_TYPE_WRONG));
            }

            sql = "from androidapi.model.order.CommentOrder CO where CO.media.pk =:param1 and CO.status =:param3";
            query = session.createQuery(sql);
            query.setParameter("param1", instagramMedia.getPk());
            query.setParameter("param3", OrderType.RUNNING);
            if (query.list().size() != 0) {
                return (new Response(Message.ANOTHER_COMMENT_ORDER_REGISTERED));
            }
            session.saveOrUpdate(instagramMedia);

            sql = "from androidapi.model.main.User U where U.id =:param2";
            query = session.createQuery(sql);
            query.setParameter("param2", userId);
            User user = (User) query.list().get(0);

            if (user.getDiamond() < orderPackage.getDiscountedDiamond()) {
                tx.commit();
                return (new Response(Message.ENOUGH_DIAMOND));
            }

            if (!orderPackage.isActive()) {
                tx.commit();
                return (new Response(Message.PACKAGE_DISABLE));
            }

            CommentOrder commentOrder = new CommentOrder(instagramMedia, orderPackage.getCount(), userId);
            session.saveOrUpdate(commentOrder);
            Tools.updateDiamonds(session, user, user.getDiamond() - orderPackage.getDiscountedDiamond(), LogType.COMMENT_ORDER);
            MyTransaction output = new MyTransaction(user.getDiamond(), -orderPackage.getDiscountedDiamond(), LogType.COMMENT_ORDER);
            tx.commit();
            return (new Response<MyTransaction>(output));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.COMMENT_ORDER_REGISTRATION_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/follow_order/{userId}/{packId}", method = RequestMethod.POST)
    @ResponseBody
    public Response followOrder(@PathVariable int userId, @PathVariable int packId, @RequestBody InstagramPage instagramPage) {
        System.out.println(instagramPage);
        boolean checkUser = Tools.checkUserStatus(userId);
        if (checkUser) return (new Response(Message.USER_BLOCK));
        boolean checkPage = Tools.checkPageStatus(instagramPage.getPk());
        if (checkPage) return (new Response(Message.PAGE_BLOCK));
        instagramPage.setTemporaryFull_name();

        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        String sql;
        Query query;
        try {
            tx = session.beginTransaction();
            // do some work
            sql = "from androidapi.model.packages.OrderPackage op where op.id =:param3";
            query = session.createQuery(sql);
            query.setParameter("param3", packId);
            OrderPackage orderPackage = (OrderPackage) query.list().get(0);
            if (orderPackage.getType() != PackageType.FOLLOW) {
                return (new Response(Message.PACKAGE_TYPE_WRONG));
            }

            sql = "from androidapi.model.order.FollowOrder FO where FO.page.pk =:param1 and FO.status =:param3";
            query = session.createQuery(sql);
            query.setParameter("param1", instagramPage.getPk());
            query.setParameter("param3", OrderType.RUNNING);
            if (query.list().size() != 0) {
                return (new Response(Message.ANOTHER_FOLLOW_ORDER_REGISTERED));
            }
            session.saveOrUpdate(instagramPage);

            sql = "from androidapi.model.main.User U where U.id =:param2";
            query = session.createQuery(sql);
            query.setParameter("param2", userId);
            User user = (User) query.list().get(0);

            if (user.getDiamond() < orderPackage.getDiscountedDiamond()) {
                tx.commit();
                return (new Response(Message.ENOUGH_DIAMOND));
            }

            if (!orderPackage.isActive()) {
                tx.commit();
                return (new Response(Message.PACKAGE_DISABLE));
            }

            FollowOrder followOrder = new FollowOrder(instagramPage, orderPackage.getCount(), userId);
            session.saveOrUpdate(followOrder);
            Tools.updateDiamonds(session, user, user.getDiamond() - orderPackage.getDiscountedDiamond(), LogType.FOLLOW_ORDER);
            MyTransaction output = new MyTransaction(user.getDiamond(), -orderPackage.getDiscountedDiamond(), LogType.FOLLOW_ORDER);
            tx.commit();
            return (new Response<MyTransaction>(output));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.FOLLOW_ORDER_REGISTRATION_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = {"/get_like_order_list/{userId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Response getLikeOrderList(@PathVariable int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            ArrayList<View> list = new ArrayList();
            String sql = "from androidapi.model.order.LikeOrder LO where LO.media.pk not in (select UL.mediaPk from androidapi.model.userdid.UserLiked UL where UL.userId =:param2) and LO.status =:param1 and LO.userId <>:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param2", Integer.valueOf(userId));
            query.setParameter("param1", OrderType.RUNNING);
            ArrayList<LikeOrder> likeOrderList = new ArrayList();
            for (int i = 0; i < query.list().size(); i++) {
                likeOrderList.add((LikeOrder) query.list().get(i));
            }

            Collections.sort(likeOrderList, (Comparator) (o1, o2) -> {
                LikeOrder lo1 = (LikeOrder) o1;
                LikeOrder lo2 = (LikeOrder) o2;
                return lo1.getCount() - lo1.getDone() - (lo2.getCount() - lo2.getDone());
            });
            ArrayList<LikeOrder> output = new ArrayList();
            ArrayList<Integer> ids = new ArrayList();

            for (int i = 0; (i < Integer.parseInt(Config.get("order_list_size")) / 4) && (likeOrderList.size() != 0); i++) {
                output.add(likeOrderList.get(0));
                ids.add(Integer.valueOf((likeOrderList.get(0)).getId()));
                likeOrderList.remove(0);
            }

            if (output.size() == 0) {
                tx.commit();
                return new Response(list);
            }

            sql = "from androidapi.model.order.LikeOrder LO where LO.id not in(:param3) and LO.media.pk not in(select UL.mediaPk from androidapi.model.userdid.UserLiked UL where UL.userId =:param2) and LO.status =:param1 and LO.userId not in(:param2) ORDER BY LO.registrationDate DESC";
            query = session.createQuery(sql);
            query.setParameter("param2", Integer.valueOf(userId));
            query.setParameter("param1", OrderType.RUNNING);
            query.setParameterList("param3", ids);
            query.setMaxResults(Integer.parseInt(Config.get("order_list_size")) - output.size());

            for (int j = 0; j < query.list().size(); j++) {
                output.add((LikeOrder) query.list().get(j));
            }

            for (int k = 0; k < output.size(); k++) {
                View view = new View();
                view.setOrder_id((output.get(k)).getId());
                view.setImage_url((output.get(k)).getMedia().getImage_url());
                view.setCaption(new String((output.get(k)).getMedia().getTemporaryCaption(), "UTF-8"));
                view.setPk((output.get(k)).getMedia().getPk());
                view.setFull_name((output.get(k)).getMedia().getFullName());
                view.setUsername((output.get(k)).getMedia().getUserName());
                list.add(view);
            }
            tx.commit();
            return new Response(list);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.GET_LIKE_ORDER_LIST_EXCEPTION);
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = {"/get_comment_order_list/{userId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Response getCommentOrderList(@PathVariable int userId) {
        Session session = androidapi.database.DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            ArrayList<View> list = new ArrayList();
            String sql = "from androidapi.model.order.CommentOrder CO where CO.media.pk not in (select UC.mediaPk from androidapi.model.userdid.UserCommented UC where UC.userId =:param2) and CO.status =:param1 and CO.userId not in(:param2)";
            Query query = session.createQuery(sql);
            query.setParameter("param2", Integer.valueOf(userId));
            query.setParameter("param1", androidapi.model.order.OrderType.RUNNING);
            ArrayList<CommentOrder> commentOrderList = new ArrayList();
            for (int i = 0; i < query.list().size(); i++) {
                commentOrderList.add((CommentOrder) query.list().get(i));
            }

            java.util.Collections.sort(commentOrderList, (Comparator) (o1, o2) -> {
                CommentOrder co1 = (CommentOrder) o1;
                CommentOrder co2 = (CommentOrder) o2;
                return co1.getCount() - co1.getDone() - (co2.getCount() - co2.getDone());
            });
            ArrayList<CommentOrder> output = new ArrayList();
            ArrayList<Integer> ids = new ArrayList();

            for (int i = 0; (i < Integer.parseInt(Config.get("order_list_size")) / 4) && (commentOrderList.size() != 0); i++) {
                output.add(commentOrderList.get(0));
                ids.add(Integer.valueOf((commentOrderList.get(0)).getId()));
                commentOrderList.remove(0);
            }

            if (output.size() == 0) {
                tx.commit();
                return new Response(list);
            }

            sql = "from androidapi.model.order.CommentOrder CO where CO.id not in :param3 and CO.media.pk not in (select UC.mediaPk from androidapi.model.userdid.UserCommented UC where UC.userId =:param2) and CO.status =:param1 and CO.userId not in(:param2) ORDER BY CO.registrationDate DESC";
            query = session.createQuery(sql);
            query.setParameter("param2", Integer.valueOf(userId));
            query.setParameter("param1", androidapi.model.order.OrderType.RUNNING);
            query.setParameterList("param3", ids);
            query.setMaxResults(Integer.parseInt(Config.get("order_list_size")) - output.size());

            for (int j = 0; j < query.list().size(); j++) {
                output.add((CommentOrder) query.list().get(j));
            }

            for (int k = 0; k < output.size(); k++) {
                View view = new View();
                view.setOrder_id((output.get(k)).getId());
                view.setImage_url((output.get(k)).getMedia().getImage_url());
                view.setCaption(new String((output.get(k)).getMedia().getTemporaryCaption(), "UTF-8"));
                view.setPk((output.get(k)).getMedia().getPk());
                view.setFull_name((output.get(k)).getMedia().getFullName());
                view.setUsername((output.get(k)).getMedia().getUserName());
                view.setCommentList((output.get(k)).getMedia().getCommentList());
                list.add(view);
            }
            tx.commit();
            return new Response(list);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.GET_COMMENT_ORDER_LIST_EXCEPTION);
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = {"/get_follow_order_list/{userId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Response getFollowOrderList(@PathVariable int userId) {
        Session session = androidapi.database.DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            ArrayList<View> list = new ArrayList();
            String sql = "from androidapi.model.order.FollowOrder FO where FO.page.pk not in (select UF.pagePk from androidapi.model.userdid.UserFollowed UF where UF.userId =:param2 and UF.followed=:param3) and FO.status =:param1 and FO.userId not in(:param2)";
            Query query = session.createQuery(sql);
            query.setParameter("param2", Integer.valueOf(userId));
            query.setParameter("param3", Boolean.valueOf(true));
            query.setParameter("param1", androidapi.model.order.OrderType.RUNNING);
            ArrayList<FollowOrder> followOrderList = new ArrayList();
            for (int i = 0; i < query.list().size(); i++) {
                followOrderList.add((FollowOrder) query.list().get(i));
            }

            java.util.Collections.sort(followOrderList, (Comparator) (o1, o2) -> {
                FollowOrder fo1 = (FollowOrder) o1;
                FollowOrder fo2 = (FollowOrder) o2;
                return fo1.getCount() - fo1.getDone() - (fo2.getCount() - fo2.getDone());
            });
            ArrayList<FollowOrder> output = new ArrayList();
            ArrayList<Integer> ids = new ArrayList();

            for (int i = 0; (i < Integer.parseInt(Config.get("order_list_size")) / 4) && (followOrderList.size() != 0); i++) {
                output.add(followOrderList.get(0));
                ids.add(Integer.valueOf((followOrderList.get(0)).getId()));
                followOrderList.remove(0);
            }

            if (output.size() == 0) {
                tx.commit();
                return new Response(list);
            }

            sql = "from androidapi.model.order.FollowOrder FO where FO.id not in :param3 and FO.page.pk not in (select UF.pagePk from androidapi.model.userdid.UserFollowed UF where UF.userId =:param2) and FO.status =:param1 and FO.userId not in(:param2) ORDER BY FO.registrationDate DESC";
            query = session.createQuery(sql);
            query.setParameter("param2", Integer.valueOf(userId));
            query.setParameter("param1", androidapi.model.order.OrderType.RUNNING);
            query.setParameterList("param3", ids);
            query.setMaxResults(Integer.parseInt(Config.get("order_list_size")) - output.size());

            for (int j = 0; j < query.list().size(); j++) {
                output.add((FollowOrder) query.list().get(j));
            }

            for (int k = 0; k < output.size(); k++) {
                View view = new View();
                view.setOrder_id((output.get(k)).getId());
                view.setImage_url((output.get(k)).getPage().getProfilePictureURI());
                view.setPk((output.get(k)).getPage().getPk());
                view.setFull_name((output.get(k)).getPage().getFull_name());
                view.setUsername((output.get(k)).getPage().getUsername());
                list.add(view);
            }
            tx.commit();
            return new Response(list);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.GET_FOLLOW_ORDER_LIST_EXCEPTION);
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/get_like_order_history/{start}/{end}", method = RequestMethod.POST)
    @ResponseBody
    public Response getLikeOrderHistory(@PathVariable int start, @PathVariable int end, @RequestBody int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            ArrayList<OrderHistory> orderHistories = new ArrayList<OrderHistory>();
            String sql = "from androidapi.model.order.LikeOrder LO where LO.userId =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setFirstResult(start);
            query.setMaxResults(end);
            for (int i = 0; i < query.list().size(); i++) {
                LikeOrder likeOrder = ((LikeOrder) query.list().get(i));
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setCount(likeOrder.getCount());
                orderHistory.setDone(likeOrder.getDone());
                orderHistory.setExecutionDate(likeOrder.getExecutionDate());
                orderHistory.setFailed(likeOrder.getFailed());
                orderHistory.setImage_url(likeOrder.getMedia().getImage_url());
                orderHistory.setRegistrationDate(likeOrder.getRegistrationDate());
                orderHistory.setStatus(likeOrder.getStatus());
                orderHistory.setTitle(likeOrder.getMedia().getCaptionText());
                orderHistory.setUsername(likeOrder.getMedia().getUserName());
                orderHistories.add(orderHistory);
            }
            tx.commit();
            return new Response(orderHistories);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_LIKE_ORDER_HISTORY_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/get_comment_order_history/{start}/{end}", method = RequestMethod.POST)
    @ResponseBody
    public Response getCommentOrderHistory(@PathVariable int start, @PathVariable int end, @RequestBody int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            ArrayList<OrderHistory> orderHistories = new ArrayList<OrderHistory>();
            String sql = "from androidapi.model.order.CommentOrder CO where CO.userId =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setFirstResult(start);
            query.setMaxResults(end);
            for (int i = 0; i < query.list().size(); i++) {
                CommentOrder commentOrder = ((CommentOrder) query.list().get(i));
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setCount(commentOrder.getCount());
                orderHistory.setDone(commentOrder.getDone());
                orderHistory.setExecutionDate(commentOrder.getExecutionDate());
                orderHistory.setFailed(commentOrder.getFailed());
                orderHistory.setImage_url(commentOrder.getMedia().getImage_url());
                orderHistory.setRegistrationDate(commentOrder.getRegistrationDate());
                orderHistory.setStatus(commentOrder.getStatus());
                orderHistory.setTitle(commentOrder.getMedia().getCaptionText());
                orderHistory.setUsername(commentOrder.getMedia().getUserName());
                orderHistories.add(orderHistory);
            }
            tx.commit();
            return new Response(orderHistories);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_COMMENT_ORDER_HISTORY_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/get_follow_order_history/{start}/{end}", method = RequestMethod.POST)
    @ResponseBody
    public Response getFollowOrderHistory(@PathVariable int start, @PathVariable int end, @RequestBody int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            ArrayList<OrderHistory> orderHistories = new ArrayList<OrderHistory>();
            String sql = "from androidapi.model.order.FollowOrder FO where FO.userId =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setFirstResult(start);
            query.setMaxResults(end);
            for (int i = 0; i < query.list().size(); i++) {
                FollowOrder followOrder = ((FollowOrder) query.list().get(i));
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setCount(followOrder.getCount());
                orderHistory.setDone(followOrder.getDone());
                orderHistory.setExecutionDate(followOrder.getExecutionDate());
                orderHistory.setFailed(followOrder.getFailed());
                orderHistory.setImage_url(followOrder.getPage().getProfilePictureURI());
                orderHistory.setRegistrationDate(followOrder.getRegistrationDate());
                orderHistory.setStatus(followOrder.getStatus());
                orderHistory.setTitle(followOrder.getPage().getFull_name());
                orderHistory.setUsername(followOrder.getPage().getUsername());
                orderHistories.add(orderHistory);
            }
            tx.commit();
            return new Response(orderHistories);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_FOLLOW_ORDER_HISTORY_EXCEPTION));
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = "/get_comment_text", method = RequestMethod.GET)
    @ResponseBody
    public Response getCommentText() {
        ArrayList<String> comments = new ArrayList<String>();
        comments.add(Config.get("comment_text1"));
        comments.add(Config.get("comment_text2"));
        comments.add(Config.get("comment_text3"));
        comments.add(Config.get("comment_text4"));
        comments.add(Config.get("comment_text5"));
        comments.add(Config.get("comment_text6"));
        comments.add(Config.get("comment_text7"));
        comments.add(Config.get("comment_text8"));
        comments.add(Config.get("comment_text9"));
        comments.add(Config.get("comment_text10"));
        return new Response(comments);
    }

    @RequestMapping(value = {"/get_all_like_orders/{userId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Response getAllLikeOrders(@PathVariable int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<View> list = new ArrayList();
            String sql = "from androidapi.model.order.LikeOrder LO where LO.media.pk not in (select UL.mediaPk from androidapi.model.userdid.UserLiked UL where UL.userId =:param2) and LO.status =:param1 and LO.userId <>:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param2", userId);
            query.setParameter("param1", OrderType.RUNNING);
            ArrayList<LikeOrder> likeOrderList = new ArrayList();
            for (int i = 0; i < query.list().size(); i++) {
                likeOrderList.add((LikeOrder) query.list().get(i));
            }

            if (likeOrderList.size() == 0) {
                tx.commit();
                return new Response(list);
            }

            for (int k = 0; k < likeOrderList.size(); k++) {
                View view = new View();
                view.setOrder_id((likeOrderList.get(k)).getId());
                view.setImage_url((likeOrderList.get(k)).getMedia().getImage_url());
                view.setCaption(new String((likeOrderList.get(k)).getMedia().getTemporaryCaption(), "UTF-8"));
                view.setPk((likeOrderList.get(k)).getMedia().getPk());
                view.setFull_name((likeOrderList.get(k)).getMedia().getFullName());
                view.setUsername((likeOrderList.get(k)).getMedia().getUserName());
                list.add(view);
            }
            tx.commit();
            return new Response(list);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.GET_ALL_LIKE_ORDERS_CLIENT_EXCEPTION);
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = {"/get_all_comment_orders/{userId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Response getAllCommentOrders(@PathVariable int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<View> list = new ArrayList();
            String sql = "from androidapi.model.order.CommentOrder CO where CO.media.pk not in (select UC.mediaPk from androidapi.model.userdid.UserCommented UC where UC.userId =:param2) and CO.status =:param1 and CO.userId <>:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param2", userId);
            query.setParameter("param1", OrderType.RUNNING);
            ArrayList<CommentOrder> commentOrderList = new ArrayList();
            for (int i = 0; i < query.list().size(); i++) {
                commentOrderList.add((CommentOrder) query.list().get(i));
            }

            if (commentOrderList.size() == 0) {
                tx.commit();
                return new Response(list);
            }

            for (int k = 0; k < commentOrderList.size(); k++) {
                View view = new View();
                view.setOrder_id((commentOrderList.get(k)).getId());
                view.setImage_url((commentOrderList.get(k)).getMedia().getImage_url());
                view.setCaption(new String((commentOrderList.get(k)).getMedia().getTemporaryCaption(), "UTF-8"));
                view.setPk((commentOrderList.get(k)).getMedia().getPk());
                view.setFull_name((commentOrderList.get(k)).getMedia().getFullName());
                view.setUsername((commentOrderList.get(k)).getMedia().getUserName());
                view.setCommentList((commentOrderList.get(k)).getMedia().getCommentList());
                list.add(view);
            }
            tx.commit();
            return new Response(list);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.GET_ALL_COMMENT_ORDERS_CLIENT_EXCEPTION);
        } finally {
            session.close();
        }
    }

    @RequestMapping(value = {"/get_all_follow_orders/{userId}"}, method = {RequestMethod.GET})
    @ResponseBody
    public Response getAllFollowOrders(@PathVariable int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            ArrayList<View> list = new ArrayList();
            String sql = "from androidapi.model.order.FollowOrder FO where FO.page.pk not in (select UF.pagePk from androidapi.model.userdid.UserFollowed UF where UF.userId =:param2 and UF.followed=:param3) and FO.status =:param1 and FO.userId not in(:param2)";
            Query query = session.createQuery(sql);
            query.setParameter("param2", Integer.valueOf(userId));
            query.setParameter("param3", Boolean.valueOf(true));
            query.setParameter("param1", androidapi.model.order.OrderType.RUNNING);
            ArrayList<FollowOrder> followOrderList = new ArrayList();
            for (int i = 0; i < query.list().size(); i++) {
                followOrderList.add((FollowOrder) query.list().get(i));
            }

            if (followOrderList.size() == 0) {
                tx.commit();
                return new Response(list);
            }

            for (int k = 0; k < followOrderList.size(); k++) {
                View view = new View();
                view.setOrder_id((followOrderList.get(k)).getId());
                view.setImage_url((followOrderList.get(k)).getPage().getProfilePictureURI());
                view.setPk((followOrderList.get(k)).getPage().getPk());
                view.setFull_name((followOrderList.get(k)).getPage().getFull_name());
                view.setUsername((followOrderList.get(k)).getPage().getUsername());
                list.add(view);
            }
            tx.commit();
            return new Response(list);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.GET_ALL_FOLLOW_ORDERS_CLIENT_EXCEPTION);
        } finally {
            session.close();
        }
    }
}
