package com.example.test.news.dto;

import com.example.test.news.Source;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

public class CreateNewsDto {
	@NotNull(message = "NOT_NULL")
	@NotEmpty(message = "NOT_EMPTY")
	private final String title;

	@NotNull(message = "NOT_NULL")
	@NotEmpty(message = "NOT_EMPTY")
	private final String url;

	@NotNull(message = "NOT_NULL")
	@NotEmpty(message = "NOT_EMPTY")
	@Past(message = "DATE_IN_PAST")
	private final Date publishDate;

	@NotNull(message = "NOT_NULL")
	private final Source source;

	@NotNull(message = "NOT_NULL")
	@NotEmpty(message = "NOT_EMPTY")
	private final String type;

	public CreateNewsDto(String title, String url, Date publishDate, Source source, String type) {
		this.title = title;
		this.url = url;
		this.publishDate = publishDate;
		this.source = source;
		this.type = type;
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
