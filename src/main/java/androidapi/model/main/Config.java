package androidapi.model.main;

import androidapi.database.DbConnector;
import androidapi.model.instagramapi.InstagramPage;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Amir Hossein on 7/24/2017.
 */
public class Config {
    private Properties prop = new Properties();
    private BufferedReader input = null;
    private OutputStream output = null;
    private HashMap<String, String> map = new HashMap<>();
    private static Config instance;

    public static Config instance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Config() {

        try {
            input = new BufferedReader(new InputStreamReader(
                    new FileInputStream("src" + File.separator + "main" + File.separator + "resources" + File.separator + "config.properties"), "UTF-8"));
            prop.load(input);
            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                map.put(key, value);
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String prop) {

        Session session = DbConnector.connectingHibernate().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // do some work
            String sql = "select CM.value from androidapi.model.main.ConfigModel CM where CM.key =:param";
            Query query = session.createQuery(sql);
            query.setParameter("param", prop);
            return (String) query.list().get(0);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            tx.commit();
            session.close();
        }
        return "Error";
//        return instance().map.get(prop);
    }

    public static String get(String prop, String defaultValue) {
        try {
            return instance().map.get(prop);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void set(String prop, String value) {
        Config conf = instance();
        conf.prop.put(prop, value);
        try {
            conf.output = new FileOutputStream("config.properties");
            conf.prop.store(conf.output, null);
            conf.map.put(prop, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (conf.output != null) {
            try {
                conf.output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String, String> getMap() {
        return map;
    }
}
