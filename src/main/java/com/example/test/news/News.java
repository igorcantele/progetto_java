package com.example.test.news;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document()
public class News {

	@Field()
	private final String title;
	@Field()
	private final String url;
	@Field()
	private final Date publishDate;
	@Field()
	private final Source source;
	@Field()
	private final String type;
	@Id
	private String id;

	public News(String title, String url, Date publishDate, String type, Source source) {
		this.title = title;
		this.url = url;
		this.publishDate = publishDate;
		this.source = source;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public Source getSource() {
		return source;
	}

	public String getType() {
		return type;
	}
}