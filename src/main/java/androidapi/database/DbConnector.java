package androidapi.database;

import androidapi.model.gift.GiftCode;
import androidapi.model.gift.GiftUsed;
import androidapi.model.instagramapi.InstagramMedia;
import androidapi.model.instagramapi.InstagramPage;
import androidapi.model.logutil.PurchaseLog;
import androidapi.model.logutil.TransactionLog;
import androidapi.model.main.ConfigModel;
import androidapi.model.main.News;
import androidapi.model.main.User;
import androidapi.model.notification.NotifIdentifier;
import androidapi.model.order.CommentOrder;
import androidapi.model.order.FollowOrder;
import androidapi.model.order.LikeOrder;
import androidapi.model.packages.OrderPackage;
import androidapi.model.packages.SalePackage;
import androidapi.model.report.CommentOrderReport;
import androidapi.model.report.FollowOrderReport;
import androidapi.model.report.LikeOrderReport;
import androidapi.model.userdid.UserCommented;
import androidapi.model.userdid.UserFollowed;
import androidapi.model.userdid.UserLiked;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class DbConnector {
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    private final static String IP = "127.0.0.1";
    private final static String PORT = "3306";
    private final static String DB_NAME = "wallinsta_app";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
//    private final static String DIALECT = "org.hibernate.dialect.MySQL5Dialect";
    private final static String url = "jdbc:mysql://" + IP + ":" + PORT + "/" + DB_NAME;
    private static SessionFactory sessionFactory;

    public static SessionFactory connectingHibernate() {
        try {
            if (sessionFactory == null || sessionFactory.isClosed()) {
                Properties database = new Properties();
                database.setProperty("hibernate.connection.driver_class", PROPERTY_NAME_DATABASE_DRIVER);
                database.setProperty("hibernate.connection.username", USERNAME);
                database.setProperty("hibernate.connection.password", PASSWORD);
                database.setProperty("hibernate.connection.url", url);
//                database.setProperty("hibernate.dialect", DIALECT);
                database.setProperty("hibernate.show_sql", "true");
                database.setProperty("hibernate.hbm2ddl.auto", "update");
                database.setProperty("hibernate.connection.CharSet", "utf8mb4_persian_ci");
                database.setProperty("hibernate.connection.characterEncoding", "UTF-8");
                database.setProperty("hibernate.connection.useUnicode", "true");
                database.setProperty("hibernate.connection.autocommit", "true");
                database.setProperty("hibernate.c3p0.min_size", "5");
                database.setProperty("hibernate.c3p0.max_size", "20");
                database.setProperty("hibernate.c3p0.timeout", "300");
                database.setProperty("hibernate.c3p0.max_statements", "50");
                database.setProperty("hibernate.c3p0.idle_test_period", "3000");
                database.setProperty("hibernate.c3p0.preferredTestQuery", "select 1");
                database.setProperty("hibernate.connection.release_mode", "after_transaction");

                Configuration cfg = new Configuration()
                        .setProperties(database)
                        .addPackage("androidapi.model")
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(InstagramPage.class)
                        .addAnnotatedClass(OrderPackage.class)
                        .addAnnotatedClass(SalePackage.class)
                        .addAnnotatedClass(News.class)
                        .addAnnotatedClass(CommentOrder.class)
                        .addAnnotatedClass(FollowOrder.class)
                        .addAnnotatedClass(InstagramMedia.class)
                        .addAnnotatedClass(LikeOrder.class)
                        .addAnnotatedClass(TransactionLog.class)
                        .addAnnotatedClass(PurchaseLog.class)
                        .addAnnotatedClass(UserLiked.class)
                        .addAnnotatedClass(UserCommented.class)
                        .addAnnotatedClass(UserFollowed.class)
                        .addAnnotatedClass(NotifIdentifier.class)
                        .addAnnotatedClass(LikeOrderReport.class)
                        .addAnnotatedClass(CommentOrderReport.class)
                        .addAnnotatedClass(ConfigModel.class)
                        .addAnnotatedClass(GiftCode.class)
                        .addAnnotatedClass(GiftUsed.class)
                        .addAnnotatedClass(FollowOrderReport.class);

                StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder()
                        .applySettings(cfg.getProperties());
                sessionFactory = cfg.buildSessionFactory(ssrb.build());
            }
            return sessionFactory;
        } catch (Exception e) {
            return DbConnector.connectingHibernate();
        }
    }
}