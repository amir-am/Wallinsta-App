package androidapi.client;

import androidapi.model.gift.GiftCode;
import androidapi.model.logutil.LogType;
import androidapi.model.main.*;
import androidapi.database.DbConnector;
import androidapi.model.bazarapi.*;
import androidapi.model.instagramapi.InstagramPage;
import androidapi.model.logutil.PurchaseLog;
import androidapi.model.notification.NotifIdentifier;
import androidapi.model.order.*;
import androidapi.model.packages.SalePackage;
import androidapi.model.report.CommentOrderReport;
import androidapi.model.report.FollowOrderReport;
import androidapi.model.report.LikeOrderReport;
import androidapi.model.userdid.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Amir Hossein on 7/17/2017.
 */
@RestController
@RequestMapping("/user_controller")
public class UserController {
    @RequestMapping(value = "/login/{app_ver}/{securityKey}", method = RequestMethod.POST)
    @ResponseBody
    public Response login(@PathVariable String securityKey, @PathVariable int app_ver, @RequestBody InstagramPage instagramPage) {
        System.out.println(instagramPage);
        String password = Config.get("security_key") + instagramPage.getPk();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new Response(Message.HASHING_EXCEPTION);
        }
        md.update(password.getBytes());
        byte byteData[] = md.digest();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        String encryptedString = sb.toString();

        if (encryptedString.equals(securityKey)) {

            Session session = DbConnector.connectingHibernate().openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                // do some work
                instagramPage.setTemporaryFull_name();
                session.saveOrUpdate(instagramPage);
                String sql = "from androidapi.model.main.User U where U.pk =:param";
                Query query = session.createQuery(sql);
                query.setParameter("param", instagramPage.getPk());
                if (query.list().size() == 0) {
                    User user = new User();
                    user.setPk(instagramPage.getPk());
                    user.setAppVersion(app_ver);
                    session.saveOrUpdate(user);
                    return (new Response<>(user));
                } else {
                    User user = (User) query.list().get(0);
                    user.setAppVersion(app_ver);
                    session.saveOrUpdate(user);
                    return new Response<>(user);
                }
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                return (new Response(Message.LOGIN_EXCEPTION));
            } finally {
                tx.commit();
                session.close();
            }
        } else {
            return new Response(Message.ILLEGALL_USE);
        }
    }

    @RequestMapping(value = "/get_diamonds/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public Response getDiamonds(@PathVariable Integer userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            if (query.list().size() == 0) {
                return (new Response(Message.USER_DOESNT_EXIST));
            }
            User user = (User) query.list().get(0);
            return new Response<Integer>((Integer) (user.getDiamond()));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_DIAMOND_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/like/{securityKey}", method = RequestMethod.POST)
    @ResponseBody
    public Response like(@PathVariable String securityKey, @RequestBody DoingDetail doingDetail) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            if (Tools.checkUserStatus(doingDetail.getUser_id())) return (new Response(Message.USER_BLOCK));
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.id =:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param2", doingDetail.getUser_id());
            User user = (User) query.list().get(0);

            sql = "from androidapi.model.order.LikeOrder LO where LO.id =:param1";
            query = session.createQuery(sql);
            query.setParameter("param1", doingDetail.getOrder_id());
            LikeOrder likeOrder = (LikeOrder) query.list().get(0);

            String password = Config.get("security_key") + doingDetail.getUser_id() + doingDetail.getOrder_id() + likeOrder.getMedia().getPk();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            String encryptedString = sb.toString();

            if (encryptedString.equals(securityKey)) {

                if (likeOrder.getStatus() == OrderType.FINISHED) {
                    return (new Response(Message.ORDER_DONE));
                }

//                sql = "from androidapi.model.userdid.UserLiked UL where UL.userId =:param4 and UL.mediaPk =:param3";
//                query = session.createQuery(sql);
//                query.setParameter("param3", likeOrder.getMedia().getPk());
//                query.setParameter("param4", doingDetail.getUser_id());
//                if (query.list().size() != 0) {
//                    return (new Response(Message.YOU_LIKED_MEDIA_BEFORE));
//                }


                Tools.updateDiamonds(session, user, user.getDiamond() + Integer.parseInt(Config.get("diamond_per_like")), LogType.LIKE);
                likeOrder.setDone(likeOrder.getDone() + 1);
                UserLiked userLiked = new UserLiked(likeOrder.getMedia().getPk(), doingDetail.getUser_id(), doingDetail.getOrder_id());
                session.saveOrUpdate(userLiked);

                MyTransaction output = new MyTransaction(user.getDiamond(), Integer.parseInt(Config.get("diamond_per_like")), LogType.LIKE);
                if (likeOrder.getDone() == likeOrder.getCount()) {
                    likeOrder.setStatus(OrderType.FINISHED);
                    likeOrder.setExecutionDate(Calendar.getInstance());
                    Tools.pushNotificationToPlayers(likeOrder.getUserId(), Message.NOTIFICATION_LIKE_DONE_TITLE, Message.NOTIFICATION_LIKE_DONE_MESSAGE);
                }
                session.saveOrUpdate(likeOrder);
                return (new Response<MyTransaction>(output));
            } else {
                return new Response(Message.ILLEGALL_USE);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new Response(Message.HASHING_EXCEPTION);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.LIKE_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }

    }

    @RequestMapping(value = "/comment/{securityKey}", method = RequestMethod.POST)
    @ResponseBody
    public Response comment(@PathVariable String securityKey, @RequestBody DoingDetail doingDetail) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            if (Tools.checkUserStatus(doingDetail.getUser_id())) return (new Response(Message.USER_BLOCK));
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.id =:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param2", doingDetail.getUser_id());
            User user = (User) query.list().get(0);

            sql = "from androidapi.model.order.CommentOrder CO where CO.id =:param1";
            query = session.createQuery(sql);
            query.setParameter("param1", doingDetail.getOrder_id());
            CommentOrder commentOrder = (CommentOrder) query.list().get(0);

            String password = Config.get("security_key") + doingDetail.getUser_id() + doingDetail.getOrder_id() + commentOrder.getMedia().getPk();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            String encryptedString = sb.toString();

            if (encryptedString.equals(securityKey)) {

                if (commentOrder.getStatus() == OrderType.FINISHED) {
                    return (new Response(Message.ORDER_DONE));
                }

//                sql = "from androidapi.model.userdid.UserCommented UC where UC.userId =:param4 and  UC.mediaPk =:param3";
//                query = session.createQuery(sql);
//                query.setParameter("param3", commentOrder.getMedia().getPk());
//                query.setParameter("param4", doingDetail.getUser_id());
//                if (query.list().size() != 0) {
//                    return (new Response(Message.YOU_COMMENTED_MEDIA_BEFORE));
//                }

                Tools.updateDiamonds(session, user, user.getDiamond() + Integer.parseInt(Config.get("diamond_per_comment")), LogType.COMMENT);
                commentOrder.setDone(commentOrder.getDone() + 1);
                UserCommented userCommented = new UserCommented(commentOrder.getMedia().getPk(), doingDetail.getUser_id(), doingDetail.getOrder_id());
                session.saveOrUpdate(userCommented);

                MyTransaction output = new MyTransaction(user.getDiamond(), Integer.parseInt(Config.get("diamond_per_comment")), LogType.COMMENT);
                if (commentOrder.getDone() == commentOrder.getCount()) {
                    commentOrder.setStatus(OrderType.FINISHED);
                    commentOrder.setExecutionDate(Calendar.getInstance());
                    Tools.pushNotificationToPlayers(commentOrder.getUserId(), Message.NOTIFICATION_COMMENT_DONE_TITLE, Message.NOTIFICATION_COMMENT_DONE_MESSAGE);
                }
                session.saveOrUpdate(commentOrder);
                return (new Response<MyTransaction>(output));
            } else {
                return new Response(Message.ILLEGALL_USE);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new Response(Message.HASHING_EXCEPTION);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.COMMENT_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/follow/{securityKey}", method = RequestMethod.POST)
    @ResponseBody
    public Response follow(@PathVariable String securityKey, @RequestBody DoingDetail doingDetail) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            if (Tools.checkUserStatus(doingDetail.getUser_id())) return (new Response(Message.USER_BLOCK));
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.id =:param2";
            Query query = session.createQuery(sql);
            query.setParameter("param2", doingDetail.getUser_id());
            User user = (User) query.list().get(0);

            sql = "from androidapi.model.order.FollowOrder FO where FO.id =:param1";
            query = session.createQuery(sql);
            query.setParameter("param1", doingDetail.getOrder_id());
            FollowOrder followOrder = (FollowOrder) query.list().get(0);

            String password = Config.get("security_key") + doingDetail.getUser_id() + doingDetail.getOrder_id() + followOrder.getPage().getPk();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            String encryptedString = sb.toString();

            if (encryptedString.equals(securityKey)) {

                if (followOrder.getStatus() == OrderType.FINISHED) {
                    return (new Response(Message.ORDER_DONE));
                }

                sql = "from androidapi.model.userdid.UserFollowed UF where UF.userId =:param4 and  UF.pagePk =:param3";
                query = session.createQuery(sql);
                query.setParameter("param3", followOrder.getPage().getPk());
                query.setParameter("param4", doingDetail.getUser_id());
                if (query.list().size() != 0) {
                    return (new Response(Message.YOU_FOLLOWED_PAGE_BEFORE));
                }

                Tools.updateDiamonds(session, user, user.getDiamond() + Integer.parseInt(Config.get("diamond_per_follow")), LogType.FOLLOW);
                followOrder.setDone(followOrder.getDone() + 1);
                UserFollowed userFollowed = new UserFollowed(followOrder.getPage().getPk(), doingDetail.getUser_id(), doingDetail.getOrder_id());
                userFollowed.setUserName(followOrder.getPage().getUsername());
                session.saveOrUpdate(userFollowed);

                MyTransaction output = new MyTransaction(user.getDiamond(), Integer.parseInt(Config.get("diamond_per_follow")), LogType.FOLLOW);
                if (followOrder.getDone() == followOrder.getCount()) {
                    followOrder.setStatus(OrderType.FINISHED);
                    followOrder.setExecutionDate(Calendar.getInstance());
                    Tools.pushNotificationToPlayers(followOrder.getUserId(), Message.NOTIFICATION_FOLLOW_DONE_TITLE, Message.NOTIFICATION_FOLLOW_DONE_MESSAGE);
                }
                session.saveOrUpdate(followOrder);
                return (new Response<MyTransaction>(output));
            } else {
                return new Response(Message.ILLEGALL_USE);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new Response(Message.HASHING_EXCEPTION);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.FOLLOW_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/unfollow/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public Response unFollow(@PathVariable int userId, @RequestBody ArrayList<Long> pagePkList) {
//        ArrayList<Long> pagePkList = new ArrayList<Long>();
//        for (int i = 0; i < userList.size(); i++) {
//            pagePkList.add(userList.get(i).getPk());
//        }
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        String sql;
        Query query;
        try {
            tx = session.beginTransaction();
            // do some work
            sql = "from androidapi.model.userdid.UserFollowed UF where UF.userId =:param2 and UF.followed =:param3 and UF.pagePk in :param1";
            query = session.createQuery(sql);
            query.setParameterList("param1", pagePkList);
            query.setParameter("param2", userId);
            query.setParameter("param3", true);
            List<UserFollowed> list = query.list();
//            ArrayList<UserFollowed> list = new ArrayList<UserFollowed>();
//
//            int asdfasa=query.list().size();
//            for (int i = 0; i < query.list().size(); i++) {
//                list.add((UserFollowed) query.list().get(0));
//            }
            MyTransaction myTransaction = new MyTransaction();
            myTransaction.setType(LogType.UNFOLLOW_MISS);
            int change = 0;

            sql = "from androidapi.model.main.User U where U.id =:param5";
            query = session.createQuery(sql);
            query.setParameter("param5", userId);
            User userMissed = (User) query.list().get(0);

            List<Integer> orderIdList = new ArrayList();
            for (UserFollowed element : list) {
                element.setFollowed(false);
                element.setUnfollowDate(Calendar.getInstance().getTime());
                orderIdList.add(element.getOrderId());
                change += Integer.parseInt(Config.get("diamond_missing_per_unfollow"));
            }
            Tools.updateDiamonds(session, userMissed, userMissed.getDiamond() - change, LogType.UNFOLLOW_MISS);

            if (orderIdList.size() == 0) {
                return new Response(Message.PAGES_DOESNT_EXIST_TO_UNFOLLOW);
            }

            sql = "from androidapi.model.order.FollowOrder FO where FO.id in :param4";
            query = session.createQuery(sql);
            query.setParameterList("param4", orderIdList);

            for (int i = 0; i < query.list().size(); i++) {
                FollowOrder element = (FollowOrder) query.list().get(i);
                element.setFailed(element.getFailed() + 1);
                session.saveOrUpdate(element);
                sql = "from androidapi.model.main.User U where U.id =:param6";
                query = session.createQuery(sql);
                query.setParameter("param6", element.getUserId());
                User userGet = (User) query.list().get(0);
                Tools.updateDiamonds(session, userGet, userGet.getDiamond() + Integer.parseInt(Config.get("diamond_back_per_unfollow")), LogType.UNFOLLOW_GET);
                session.saveOrUpdate(userGet);
            }
            myTransaction.setChange(-change);
            myTransaction.setDiamond(userMissed.getDiamond());
            return (new Response<MyTransaction>(myTransaction));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.UNFOLLOW_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/check_purchase_status/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public Response checkPurchaseStatus(@PathVariable int userId, @RequestBody BuyingDetail buyingDetail) {
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        BazarSpecification bazarSpecification = new BazarSpecification("refresh_token", "viO9337dP4YFSIHJf6zhlaX9y3AoU7I5MS7AZQ7l", "LOtzsgHVegAwCYFHdjD1CgvtXjuiqhJfcSn45HCVupaCdlwbzjm5UD8bzCeV", "YPgL2x6aZOeD9SSCGGOeMr9YbCRxBR");
        AccessTokenResponse accessTokenResponse = restTemplate.postForObject("http://pardakht.cafebazaar.ir/auth/token/?grant_type=" + bazarSpecification.getGrant_type() + "&client_id=" + bazarSpecification.getClient_id() + "&client_secret=" + bazarSpecification.getClient_secret() + "&refresh_token=" + bazarSpecification.getRefresh_token(), "", AccessTokenResponse.class);

        String packageName = "ir.atitec.wallinsta";
        String url = "https://pardakht.cafebazaar.ir/devapi/v2/api/validate/" + packageName + "/inapp/" + buyingDetail.getProductId() + "/purchases/" + buyingDetail.getPurchaseToken() + "/?access_token=" + accessTokenResponse.getAccess_token();
        PurchaseCheckResponse purchaseCheckResponse = restTemplate.getForObject(url, PurchaseCheckResponse.class);

        PurchaseLog purchaseLog = new PurchaseLog(buyingDetail.getPurchaseToken(), buyingDetail.getProductId(), userId, purchaseCheckResponse.getPurchaseState());
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            session.saveOrUpdate(purchaseLog);
            if (purchaseCheckResponse.getPurchaseState() == 0) {
                String sql = "from androidapi.model.packages.SalePackage SP where SP.sku =:param1";
                Query query = session.createQuery(sql);
                query.setParameter("param1", buyingDetail.getProductId());
                SalePackage salePackage = (SalePackage) query.list().get(0);
                Tools.generateAndSendEmail(salePackage.getPrice());

                sql = "from androidapi.model.main.User U where U.id =:param2";
                query = session.createQuery(sql);
                query.setParameter("param2", userId);
                User user = (User) query.list().get(0);

                Tools.updateDiamonds(session, user, user.getDiamond() + salePackage.getDiamond(), LogType.PURCHASE_DIAMOND);
                session.saveOrUpdate(user);

                MyTransaction myTransaction = new MyTransaction(user.getDiamond(), salePackage.getDiamond(), LogType.PURCHASE_DIAMOND);
                return new Response<>(myTransaction);
            } else return new Response(Message.PURCHASE_OPERATION_EXCEPTION_FROM_BAZAR);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.PURCHASE_OPERATION_EXCEPTION_FROM_SERVER));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_liked_list/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Response getLikedList(@PathVariable int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, Integer.parseInt(Config.get("support_deadline")));
            Date date = c.getTime();
            String sql = "from androidapi.model.userdid.UserLiked UL where UL.userId =:param and UL.liked=:param3 and UL.likeDate > :param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setParameter("param2", date);
            query.setParameter("param3", true);
            return new Response(query.list());
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_LIKED_LIST_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_commented_list/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Response getCommentedList(@PathVariable int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, Integer.parseInt(Config.get("support_deadline")));
            Date date = c.getTime();
            String sql = "from androidapi.model.userdid.UserCommented UC where UC.userId =:param and UC.commented=:param3 and UC.commentDate > :param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setParameter("param2", date);
            query.setParameter("param3", true);
            return new Response(query.list());
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_COMMENTED_LIST_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_followed_list/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Response getFollowedList(@PathVariable int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, Integer.parseInt(Config.get("support_deadline")));
            Date date = c.getTime();
            String sql = "from androidapi.model.userdid.UserFollowed UF where UF.userId =:param and UF.followed=:param3 and UF.followDate > :param2";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
            query.setParameter("param2", date);
            query.setParameter("param3", true);
            return new Response(query.list());
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_FOLLOWED_LIST_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/send_diamond", method = RequestMethod.POST)
    @ResponseBody
    public Response sendDiamond(@RequestBody Transfer transfer) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.main.User U where U.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", transfer.getUserId());
            User userMiss = (User) query.list().get(0);

            sql = "from androidapi.model.main.User U where U.pk =:param";
            query = session.createQuery(sql);
            query.setParameter("param", transfer.getDestinationPk());
            User userGet = (User) query.list().get(0);

            if (userMiss.getPk() == userGet.getPk()) {
                return (new Response(Message.SEND_DIAMOND_TO_YOURSELF));
            }

            if (userMiss.getDiamond() >= (transfer.getCount() + Integer.parseInt(Config.get("diamond_miss_per_transfer")))) {
                Tools.updateDiamonds(session, userMiss, userMiss.getDiamond() - transfer.getCount() - Integer.parseInt(Config.get("diamond_miss_per_transfer")), LogType.SEND_DIAMOND);
                Tools.updateDiamonds(session, userGet, userGet.getDiamond() + transfer.getCount(), LogType.RECIEVE_DIAMOND_FROM_USER);
                MyTransaction myTransaction = new MyTransaction(userMiss.getDiamond(), -transfer.getCount() - Integer.parseInt(Config.get("diamond_miss_per_transfer")), LogType.SEND_DIAMOND);
                return (new Response<>(myTransaction));
            } else {
                return (new Response(Message.ENOUGH_DIAMOND));
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.SEND_DIAMOND_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/reg_one_signal", method = RequestMethod.POST)
    @ResponseBody
    public Response registerOneSignal(@RequestBody NotifIdentifier notifIdentifier) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "from androidapi.model.notification.NotifIdentifier N where N.oneSignalId =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", notifIdentifier.getOneSignalId());
            if (query.list().size() != 0) {
                NotifIdentifier current = (NotifIdentifier) query.list().get(0);
                current.setUserId(notifIdentifier.getUserId());
                session.saveOrUpdate(current);
            } else {
                session.saveOrUpdate(notifIdentifier);
            }
            return new Response(true);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.REGISTER_ONE_SIGNAL_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/get_username", method = RequestMethod.POST)
    @ResponseBody
    public Response getUsername(@RequestBody long pagePk) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work

            String sql = "from androidapi.model.instagramapi.InstagramPage IP where IP.pk =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", pagePk);
            if (query.list().size() == 0) {
                return new Response(Message.PAGE_DOESNT_EXIST);
            } else {
                InstagramPage instagramPage = (InstagramPage) query.list().get(0);
                instagramPage.setTemporaryFull_name();
                return (new Response(instagramPage));
            }

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.GET_USERNAME_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/like_order_report", method = RequestMethod.POST)
    @ResponseBody
    public Response likeOrderReport(@RequestBody LikeOrderReport likeOrderReport) {
        likeOrderReport.setTemporaryText();
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(likeOrderReport);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.LIKE_ORDER_REPORT_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
        return new Response<Boolean>(true);
    }

    @RequestMapping(value = "/comment_order_report", method = RequestMethod.POST)
    @ResponseBody
    public Response commentOrderReport(@RequestBody CommentOrderReport commentOrderReport) {
        commentOrderReport.setTemporaryText();
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(commentOrderReport);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.COMMENT_ORDER_REPORT_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
        return new Response<Boolean>(true);
    }

    @RequestMapping(value = "/follow_order_report", method = RequestMethod.POST)
    @ResponseBody
    public Response followOrderReport(@RequestBody FollowOrderReport followOrderReport) {
        followOrderReport.setTemporaryText();
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(followOrderReport);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return (new Response(Message.FOLLOW_ORDER_REPORT_EXCEPTION));
        } finally {
            tx.commit();
            session.close();
        }
        return new Response<Boolean>(true);
    }

    @RequestMapping(value = "/initial", method = RequestMethod.GET)
    @ResponseBody
    public Response initial() {
        Initial initial = new Initial();
        initial.setDiamondPerLike(Integer.parseInt(Config.get("diamond_per_like")));
        initial.setDiamondPerComment(Integer.parseInt(Config.get("diamond_per_comment")));
        initial.setDiamondPerFollow(Integer.parseInt(Config.get("diamond_per_follow")));
        initial.setDiamondMissingPerUnlike(Integer.parseInt(Config.get("diamond_missing_per_unlike")));
        initial.setDiamondMissingPerUncomment(Integer.parseInt(Config.get("diamond_missing_per_uncomment")));
        initial.setDiamondMissingPerUnfollow(Integer.parseInt(Config.get("diamond_missing_per_unfollow")));
        initial.setSupportDeadline(Integer.parseInt(Config.get("support_deadline")));
        initial.setDiamondMissPerTransfer(Integer.parseInt(Config.get("diamond_miss_per_transfer")));
        initial.setSecurityKey(Config.get("security_key"));
        initial.setTelegramLink(Config.get("telegramLink"));
        initial.setInstagramLink(Config.get("instagramLink"));
        initial.setAboutUsLink(Config.get("aboutUsLink"));

        return new Response<>(initial);
    }

    @RequestMapping(value = "/check_ver", method = RequestMethod.POST)
    @ResponseBody
    public Response checkVersion(@RequestBody int currVer) {
        if (currVer < Integer.parseInt(Config.get("curr_app_ver")) && currVer > Integer.parseInt(Config.get("min_app_ver"))) {
            return new Response<VersionDetail>(new VersionDetail(false, false, Message.NEED_TO_UPDATE));
        } else if (currVer < Integer.parseInt(Config.get("curr_app_ver")) && currVer < Integer.parseInt(Config.get("min_app_ver"))) {
            return new Response<VersionDetail>(new VersionDetail(false, true, Message.FORCE_TO_UPDATE));
        } else if (currVer > Integer.parseInt(Config.get("curr_app_ver")) && currVer > Integer.parseInt(Config.get("min_app_ver"))) {
            return new Response<VersionDetail>(new VersionDetail(false, false, Message.NEED_TO_UPDATE));
        } else {
            return new Response<VersionDetail>(new VersionDetail(true, false, Message.APP_VERSION_OK));
        }
    }

    @RequestMapping(value = {"/apply_gift/{userId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Response applyGift(@PathVariable int userId, @RequestBody String code) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "from androidapi.model.gift.GiftCode GC where GC.code =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", code);

            if (query.list().size() == 0) {
                return new Response(Message.GIFT_CODE_DOESNT_EXIST);
            }
            GiftCode giftCode = (GiftCode) query.list().get(0);

            String sql1 = "from androidapi.model.gift.GiftUsed GU where GU.userId =:param1 and GU.giftCode =:param2";
            Query query1 = session.createQuery(sql1);
            query1.setParameter("param1", Integer.valueOf(userId));
            query1.setParameter("param2", giftCode);

            String sql2 = "from androidapi.model.main.User U where U.id =:param";
            Query query2 = session.createQuery(sql2);
            query2.setParameter("param", Integer.valueOf(userId));
            User userGet = (User) query2.list().get(0);
            Response localResponse2;
            if (giftCode.getTotalCount() <= giftCode.getUsedCount()) {
                return new Response(Message.GIFT_CODE_FINISHED);
            }
            if (Calendar.getInstance().getTimeInMillis() > giftCode.getExpireDate().getTimeInMillis()) {
                return new Response(Message.GIFT_CODE_EXPIRED);
            }

            if (query1.list().size() != 0) {
                return new Response(Message.GIFT_CODE_USED_BEFORE);
            }

            Tools.updateDiamonds(session, userGet, userGet.getDiamond() + giftCode.getDiamond(), LogType.GIFT_CODE);
            session.save(new androidapi.model.gift.GiftUsed(userId, giftCode));
            MyTransaction myTransaction = new MyTransaction(userGet.getDiamond(), giftCode.getDiamond(), LogType.GIFT_CODE);
            giftCode.setUsedCount(giftCode.getUsedCount() + 1);
            session.update(giftCode);
            return new Response(myTransaction, Message.GIFT_CODE_APPLY_SUCCESSFULLY);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.APPLY_GIFT_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = {"/gifts_used/{userId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.GET})
    @ResponseBody
    public Response giftsUsed(@PathVariable int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            String sql = "from androidapi.model.gift.GiftUsed GU where GU.userId =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", Integer.valueOf(userId));
            androidapi.model.gift.GiftHistory[] giftHistory = new androidapi.model.gift.GiftHistory[query.list().size()];


            for (int i = 0; i < query.list().size(); i++) {
                androidapi.model.gift.GiftUsed giftUsed = (androidapi.model.gift.GiftUsed) query.list().get(i);
                giftHistory[i] = new androidapi.model.gift.GiftHistory();
                giftHistory[i].setUseDate(giftUsed.getUseDate());
                giftHistory[i].setCode(giftUsed.getGiftCode().getCode());
                giftHistory[i].setDiamond(giftUsed.getGiftCode().getDiamond());
            }
            return new Response(giftHistory);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.GIFTS_USED_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = {"/last_view_update"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Response lastViewUpdate(@RequestBody int userId) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            String sql = "from androidapi.model.main.User U where U.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", Integer.valueOf(userId));
            User user = (User) query.list().get(0);
            user.setLastViewDate(Calendar.getInstance());
            session.update(user);
            return new Response(Boolean.valueOf(true));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.LAST_VIEW_UPDATE_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = {"/inset/{userId}"}, method = {org.springframework.web.bind.annotation.RequestMethod.POST})
    @ResponseBody
    public Response inset(@PathVariable int userId, @RequestBody long pagePk) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();


            String sql = "from androidapi.model.main.User U where U.pk =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", Long.valueOf(pagePk));
            if (query.list().size() == 0) {
                return new Response(Message.USER_DOESNT_EXIST);
            }
            User user = (User) query.list().get(0);
            Tools.updateDiamonds(session, user, Integer.parseInt(Config.get("gift_of_introducer")) + user.getDiamond(), LogType.INTRODUCER);

            sql = "from androidapi.model.main.User U where U.id =:param";
            query = session.createQuery(sql);
            query.setParameter("param", Integer.valueOf(userId));
            User user2 = (User) query.list().get(0);
            Tools.updateDiamonds(session, user2, Integer.parseInt(Config.get("gift_of_presented")) + user2.getDiamond(), LogType.PRESENTED);
            MyTransaction myTransaction = new MyTransaction(user2.getDiamond(), Integer.parseInt(Config.get("gift_of_presented")), LogType.PRESENTED);
            return new Response(myTransaction, Message.INTRODUCTION_CODE_APPLY_SUCCESSFULLY);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new Response(Message.INSET_EXCEPTION);
        } finally {
            tx.commit();
            session.close();
        }
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public Response test(@RequestBody String fuckingJson) {
        System.out.println(fuckingJson);
        return new Response<>("Fucking JSON Recieved !");
    }
}

