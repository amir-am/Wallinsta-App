package androidapi.client;

import androidapi.database.DbConnector;
import androidapi.model.instagramapi.InstagramMedia;
import androidapi.model.instagramapi.InstagramPage;
import androidapi.model.logutil.LogType;
import androidapi.model.logutil.PurchaseLog;
import androidapi.model.main.*;
import androidapi.model.notification.OneSignalDetailsWithPlayers;
import androidapi.model.order.CommentOrder;
import androidapi.model.order.FollowOrder;
import androidapi.model.order.LikeOrder;
import androidapi.model.order.OrderType;
import androidapi.model.userdid.UserFollowed;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Amir Hossein on 7/17/2017.
 */
//@CrossOrigin(origins = "http://localhost:9000")
@RestController
@RequestMapping("/admin_controller")
public class AdminController {

    @RequestMapping(value = "/send_gift_with_id/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public Response sendGiftWithId(@PathVariable int userId, @RequestBody int count) {
        org.hibernate.Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            User userGet = (User) query.list().get(0);

            Tools.updateDiamonds(session, userGet, userGet.getDiamond() + count, LogType.RECIEVE_DIAMOND_FROM_ADMIN);
            Tools.pushNotificationToPlayers(userId, Message.NOTIFICATION_GIFT_TITLE, Message.NOTIFICATION_GIFT_MESSAGE);
            session.saveOrUpdate(userGet);
            return (new Response<Boolean>(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.SEND_GIFT_WITH_ID_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/send_gift_with_pk/{pagePk}", method = RequestMethod.POST)
    @ResponseBody
    public Response sendGiftWithPk(@PathVariable long pagePk, @RequestBody int count) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.pagePk =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", pagePk);
            User userGet = (User) query.list().get(0);

            Tools.updateDiamonds(session, userGet, userGet.getDiamond() + count, LogType.RECIEVE_DIAMOND_FROM_ADMIN);
            session.saveOrUpdate(userGet);
            Tools.pushNotificationToPlayers(userGet.getId(), Message.NOTIFICATION_GIFT_TITLE, Message.NOTIFICATION_GIFT_MESSAGE);
            return (new Response<Boolean>(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.SEND_GIFT_WITH_PK_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_all_like_orders/{start}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public Response getAllLikeOrders(@PathVariable Integer start, @PathVariable Integer end) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.order.LikeOrder";
            Query query = session.createQuery(sql);
            query.setFirstResult(start);
            query.setMaxResults(end);

            ArrayList<LikeOrder> likeOrders = new ArrayList<LikeOrder>();
            for (int i = 0; i < query.list().size(); i++) {
                likeOrders.add((LikeOrder) query.list().get(i));
            }
            return (new Response<ArrayList>(likeOrders));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_ALL_LIKE_ORDERS_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_all_comment_orders/{start}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public Response getAllCommentOrders(@PathVariable Integer start, @PathVariable Integer end) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.order.CommentOrder";
            Query query = session.createQuery(sql);
            query.setFirstResult(start);
            query.setMaxResults(end);

            ArrayList<CommentOrder> commentOrders = new ArrayList<CommentOrder>();
            for (int i = 0; i < query.list().size(); i++) {
                commentOrders.add((CommentOrder) query.list().get(i));
            }
            return (new Response<ArrayList>(commentOrders));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_ALL_COMMENT_ORDERS_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_all_follow_orders/{start}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public Response getAllFollowOrders(@PathVariable Integer start, @PathVariable Integer end) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.order.FollowOrder";
            Query query = session.createQuery(sql);
            query.setFirstResult(start);
            query.setMaxResults(end);

            ArrayList<FollowOrder> followOrders = new ArrayList<FollowOrder>();
            for (int i = 0; i < query.list().size(); i++) {
                followOrders.add((FollowOrder) query.list().get(i));
            }
            return (new Response<ArrayList>(followOrders));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_ALL_FOLLOW_ORDERS_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_all_members/{start}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public Response getAllMembers() {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User";
            Query query = session.createQuery(sql);

            ArrayList<User> users = new ArrayList<User>();
            for (int i = 0; i < query.list().size(); i++) {
                users.add((User) query.list().get(i));
            }
            return (new Response<ArrayList>(users));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_ALL_MEMBERS_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/like_order_block", method = RequestMethod.POST)
    @ResponseBody
    public Response likeOrderBlock(@RequestBody int orderId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.order.LikeOrder LO where LO.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", orderId);
            LikeOrder likeOrder = (LikeOrder) query.list().get(0);
            likeOrder.setStatus(OrderType.BLOCK);
            return (new Response<Boolean>(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.LIKE_ORDER_BLOCK_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/comment_order_block", method = RequestMethod.POST)
    @ResponseBody
    public Response commentOrderBlock(@RequestBody int orderId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.order.CommentOrder CO where CO.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", orderId);
            CommentOrder commentOrder = (CommentOrder) query.list().get(0);
            commentOrder.setStatus(OrderType.BLOCK);
            return (new Response<Boolean>(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.COMMENT_ORDER_BLOCK_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/follow_order_block", method = RequestMethod.POST)
    @ResponseBody
    public Response followOrderBlock(@RequestBody int orderId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.order.FollowOrder FO where FO.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", orderId);
            FollowOrder followOrder = (FollowOrder) query.list().get(0);
            followOrder.setStatus(OrderType.BLOCK);
            return (new Response<Boolean>(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.FOLLOW_ORDER_BLOCK_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/user_block", method = RequestMethod.POST)
    @ResponseBody
    public Response userBlock(@RequestBody int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            User user = (User) query.list().get(0);
            user.setBlock(true);
            return (new Response<Boolean>(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.USER_BLOCK_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/media_block", method = RequestMethod.POST)
    @ResponseBody
    public Response mediaBlock(@RequestBody long pk) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.instagramapi.InstagramMedia IM where IM.pk =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", pk);
            InstagramMedia instagramMedia = (InstagramMedia) query.list().get(0);
            instagramMedia.setBlock(true);
            return (new Response<Boolean>(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.MEDIA_BLOCK_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/page_block", method = RequestMethod.POST)
    @ResponseBody
    public Response pageBlock(@RequestBody long pk) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.instagramapi.InstagramPage IP where IP.pk =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", pk);
            InstagramPage instagramPage = (InstagramPage) query.list().get(0);
            instagramPage.setBlock(true);
            return (new Response<Boolean>(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.PAGE_BLOCK_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = {"/all_block"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Response allBlock(@RequestBody long pk) {
//        pageBlock(pk);
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

//            String sql = "from androidapi.model.main.User U where U.id =:param";
//            Query query = session.createQuery(sql);
//            query.setParameter("param", userId);
//            User user = (User) query.list().get(0);
//            user.setBlock(true);
//            session.update(user);

            UserController userController = new UserController();
            InstagramPage instagramPage = (InstagramPage) userController.getUsername(pk).getData();
            String username = instagramPage.getUsername();

            String sql = "from androidapi.model.order.LikeOrder LO where LO.media.userName =:param and LO.status =:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", username);
            query.setParameter("param2", OrderType.RUNNING);
            for (int i = 0; i < query.list().size(); i++) {
                LikeOrder likeOrder = (LikeOrder) query.list().get(i);
                likeOrder.setStatus(OrderType.BLOCK);
                session.update(likeOrder);
            }

            sql = "from androidapi.model.order.CommentOrder CO where CO.media.userName =:param and CO.status =:param2";
            query = session.createQuery(sql);
            query.setParameter("param", username);
            query.setParameter("param2", OrderType.RUNNING);
            for (int i = 0; i < query.list().size(); i++) {
                CommentOrder commentOrder = (CommentOrder) query.list().get(i);
                commentOrder.setStatus(OrderType.BLOCK);
                session.update(commentOrder);
            }

            sql = "from androidapi.model.order.FollowOrder FO where FO.page.pk =:param and FO.status =:param2";
            query = session.createQuery(sql);
            query.setParameter("param", pk);
            query.setParameter("param2", OrderType.RUNNING);
            for (int i = 0; i < query.list().size(); i++) {
                FollowOrder followOrder = (FollowOrder) query.list().get(i);
                followOrder.setStatus(OrderType.BLOCK);
                session.update(followOrder);
            }

            return new Response(Boolean.valueOf(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.ALL_BLOCK_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = {"/all_block_with_user_id"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Response allBlockWithUserId(@RequestBody int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            String sql = "from androidapi.model.order.LikeOrder LO where LO.userId =:param and LO.status =:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setParameter("param2", OrderType.RUNNING);
            for (int i = 0; i < query.list().size(); i++) {
                LikeOrder likeOrder = (LikeOrder) query.list().get(i);
                likeOrder.setStatus(OrderType.BLOCK);
                session.update(likeOrder);
            }

            sql = "from androidapi.model.order.CommentOrder CO where CO.userId =:param and CO.status =:param2";
            query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setParameter("param2", OrderType.RUNNING);
            for (int i = 0; i < query.list().size(); i++) {
                CommentOrder commentOrder = (CommentOrder) query.list().get(i);
                commentOrder.setStatus(OrderType.BLOCK);
                session.update(commentOrder);
            }

            sql = "from androidapi.model.order.FollowOrder FO where FO.userId =:param and FO.status =:param2";
            query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setParameter("param2", OrderType.RUNNING);
            for (int i = 0; i < query.list().size(); i++) {
                FollowOrder followOrder = (FollowOrder) query.list().get(i);
                followOrder.setStatus(OrderType.BLOCK);
                session.update(followOrder);
            }

            return new Response(Boolean.valueOf(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.ALL_BLOCK_WITH_USERID_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_config_list", method = RequestMethod.GET)
    @ResponseBody
    public Response getConfigList() {
        return new Response(Config.instance().getMap());
    }

    @RequestMapping(value = "/set_config_item/{key}", method = RequestMethod.GET)
    @ResponseBody
    public Response setConfigItem(@PathVariable String key, @RequestBody String value) {
        Config.set(key, value);
        return new Response(true);
    }

    @RequestMapping(value = "/user_search_by_id", method = RequestMethod.POST)
    @ResponseBody
    public Response userSearchById(@RequestBody int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            if (query.list().size() == 0) {
                return new Response(Message.USER_DOESNT_EXIST);
            }
            return (new Response<User>((User) query.list().get(0)));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.USER_SEARCH_BY_ID));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/user_search_by_pk", method = RequestMethod.POST)
    @ResponseBody
    public Response userSearchByPk(@RequestBody int pagePk) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.pk =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", pagePk);
            if (query.list().size() == 0) {
                return new Response(Message.USER_DOESNT_EXIST);
            }
            return (new Response<User>((User) query.list().get(0)));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.USER_SEARCH_BY_PK));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_purchase_history", method = RequestMethod.GET)
    @ResponseBody
    public Response getPurchaseHistory() {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.logutil.PurchaseLog";
            Query query = session.createQuery(sql);
            return (new Response(query.list()));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_PURCHASE_HISTORY));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/transaction_search", method = RequestMethod.POST)
    @ResponseBody
    public Response transactionSearch(@RequestBody String purchaseToken) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.logutil.PurchaseLog PL where PL.purchaseToken =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", purchaseToken);
            if (query.list().size() == 0) {
                return new Response(Message.TOKEN_DOESNT_EXIST);
            }
            return (new Response<PurchaseLog>((PurchaseLog) query.list().get(0)));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.TRANSACTION_SEARCH_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/block_private/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public Response blockPrivate(@RequestBody long pk, @PathVariable int userId) {
        allBlock(pk);
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            String sql = "from androidapi.model.userdid.UserFollowed UF where UF.pagePk=:param and UF.userId=:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", pk);
            query.setParameter("param2", userId);
            if (query.list().size() != 0) {
                UserFollowed userFollowed = (UserFollowed) query.list().get(0);
                userFollowed.setFollowed(false);
                session.update(userFollowed);
            }
            sql = "from androidapi.model.main.User U where U.id =:param3";
            query = session.createQuery(sql);
            query.setParameter("param3", userId);
            User user = (User) query.list().get(0);
            Tools.updateDiamonds(session, user, user.getDiamond() - Integer.parseInt(Config.get("diamond_per_follow")), LogType.PRIVATE_MISS);

            return new Response(Boolean.TRUE);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.BLOCK_PRIVATE_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/notif_to_all/{title}", method = RequestMethod.POST)
    @ResponseBody
    public Response NotifToAll(@PathVariable String title, @RequestBody String message) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        ArrayList<String> playerIds = new ArrayList<>();
        try {
            tx = session.beginTransaction();
            String sql = "select N.oneSignalId from androidapi.model.notification.NotifIdentifier N";
            Query query = session.createQuery(sql);
            for (int i = 0; i < query.list().size(); i++) {
                playerIds.add((String) query.list().get(i));
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
        if (playerIds.size() == 0) {
            return new Response("User doesn't exist in db!");
        }
        OneSignalDetailsWithPlayers body = new OneSignalDetailsWithPlayers();
        body.setApp_id("2f056739-4ff1-414a-82b9-3ba026314315");
        body.setInclude_player_ids(playerIds);
        HashMap content = new HashMap();
        content.put("en", message);
        body.setContents(content);
        HashMap headings = new HashMap();
        headings.put("en", title);
        body.setHeadings(headings);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization", "Basic MjA0MTIxYjUtMDM1ZS00ZWMzLWI1NmMtZDY2OGJkZmJkYTNj");
        headers.add("Content-Type", "application/json; charset=utf-8");

        HttpEntity<OneSignalDetailsWithPlayers> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        String response = restTemplate.postForObject("https://onesignal.com/api/v1/notifications/?", request, String.class);
        return new Response(true);
    }

}
