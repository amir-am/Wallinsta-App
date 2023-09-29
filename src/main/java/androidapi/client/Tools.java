package androidapi.client;

import androidapi.database.DbConnector;
import androidapi.model.logutil.LogType;
import androidapi.model.logutil.TransactionLog;
import androidapi.model.notification.OneSignalDetailsWithPlayers;
import androidapi.model.main.User;
import androidapi.model.instagramapi.InstagramMedia;
import androidapi.model.instagramapi.InstagramPage;
import androidapi.model.notification.OneSignalDetailsWithSegments;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Created by Amir Hossein on 9/3/2017.
 */
public class Tools {
    protected static boolean checkUserStatus(int Id) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "from androidapi.model.User U where U.id =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", Id);
            User output = (User) query.list().get(0);
            if (output.isBlock()) {
                return true;
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    protected static boolean checkPageStatus(long Id) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "from androidapi.model.instagramapi.InstagramPage IP where IP.pk =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", Id);
            InstagramPage instagramPage = (InstagramPage) query.list().get(0);
            if (instagramPage.isBlock()) {
                return true;
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    protected static boolean checkMediaStatus(long Id) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String sql = "from androidapi.model.instagramapi.InstagramMedia IM where IM.pk =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", Id);
            InstagramMedia instagramMedia = (InstagramMedia) query.list().get(0);
            if (instagramMedia.isBlock()) {
                return true;
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    protected static void updateDiamonds(Session session, User user, int newAmount, LogType type) {
        TransactionLog transactionLog = new TransactionLog(user.getId(), newAmount - user.getDiamond(), type);
        user.setDiamond(newAmount);
        session.saveOrUpdate(transactionLog);
        session.saveOrUpdate(user);
    }

    protected static String pushNotificationToPlayers(int userId, String title, String message) {
        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        ArrayList<String> playerIds = new ArrayList<>();
        try {
            tx = session.beginTransaction();
            String sql = "select N.oneSignalId from androidapi.model.notification.NotifIdentifier N where N.userId =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", userId);
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
            return "User doesn't exist in db!";
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
        return response;

//        try {
//            String jsonResponse;
//
//            URL url = new URL("https://onesignal.com/api/v1/notifications");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setUseCaches(false);
//            con.setDoOutput(true);
//            con.setDoInput(true);
//
//            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            con.setRequestProperty("Authorization", "Basic MjA0MTIxYjUtMDM1ZS00ZWMzLWI1NmMtZDY2OGJkZmJkYTNj");
//            con.setRequestMethod("POST");
//
//            String strJsonBody = "{"
//                    + "\"app_id\": \"2f056739-4ff1-414a-82b9-3ba026314315\","
//                    + "\"include_player_ids\": [\"6392d91a-b206-4b7b-a620-cd68e32c3a76\",\"76ece62b-bcfe-468c-8a78-839aeaa8c5fa\",\"8e0f21fa-9a5a-4ae7-a9a6-ca1f24294b86\"],"
//                    + "\"data\": {\"foo\": \"bar\"},"
//                    + "\"contents\": {\"en\": \"English Message\"}"
//                    + "}";
//
//
//            System.out.println("strJsonBody:\n" + strJsonBody);
//
//            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
//            con.setFixedLengthStreamingMode(sendBytes.length);
//
//            OutputStream outputStream = con.getOutputStream();
//            outputStream.write(sendBytes);
//
//            int httpResponse = con.getResponseCode();
//            System.out.println("httpResponse: " + httpResponse);
//
//            if (httpResponse >= HttpURLConnection.HTTP_OK
//                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
//                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
//                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//                scanner.close();
//            } else {
//                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
//                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
//                scanner.close();
//            }
//            System.out.println("jsonResponse:\n" + jsonResponse);
//
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }

    protected static String pushNotificationToSegments(String title, String message, ArrayList<String> segments) {
        OneSignalDetailsWithSegments body = new OneSignalDetailsWithSegments();
        body.setApp_id("cabcd5e3-335c-4f95-a315-c01f1915f301");
        body.setIncluded_segments(segments);
        HashMap content = new HashMap();
        content.put("en", message);
        body.setContents(content);
        HashMap headings = new HashMap();
        headings.put("en", title);
        body.setHeadings(headings);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Authorization", "Basic MjI4Y2FlMTAtNWU2Yi00ZTQ4LTg1NDctYmQ4ZWI0NWJiODZj");
        HttpEntity<OneSignalDetailsWithSegments> request = new HttpEntity<OneSignalDetailsWithSegments>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        String response = restTemplate.postForObject("https://onesignal.com/api/v1/notifications/?", request, String.class);
        return response;


    }

    protected static void generateAndSendEmail(int n) {
        Properties mailServerProperties;
        javax.mail.Session getMailSession;
        MimeMessage generateMailMessage;
        // Step1
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        // Step2
        getMailSession = javax.mail.Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        try {
            generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("instabox2017@gmail.com"));
            generateMailMessage.setSubject("New purchase");
            String emailBody = "Date : " + Calendar.getInstance().getTime() + "<br>Price : " + n;
            generateMailMessage.setContent(emailBody, "text/html");
            // Step3
            Transport transport = getMailSession.getTransport("smtp");

            transport.connect("smtp.gmail.com", "instabox2017@gmail.com", "Myinstaapp2017");
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
