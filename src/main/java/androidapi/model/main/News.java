package androidapi.model.main;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

/**
 * Created by Amir Hossein on 7/24/2017.
 */
@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "img_url")
    private String imageUrl;
    @Column(name = "create_date")
    private Calendar createDate;
    @Column(name = "title")
    private String title;
    @Column(name = "text", length = 2048)
    private String text;
    @Column(name = "news_url")
    private String newsUrl;

    public News() {
        createDate = Calendar.getInstance();
    }

    public News(String image_url, String title, String text, String news_url) {
        this.imageUrl = image_url;
        createDate = Calendar.getInstance();
        this.title = title;
        this.text = text;
        this.newsUrl = news_url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
