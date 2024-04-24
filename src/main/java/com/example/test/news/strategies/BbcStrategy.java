package com.example.test.news.strategies;

import com.example.test.news.News;
import com.example.test.news.Source;
import com.example.test.news.strategies.exceptions.NewsFetchException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.stream.IntStream;

@Component()
public class BbcStrategy implements NewsStrategy {
	private final String uri = "http://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=9acc642023684f07b46fae89185513ce";
	private final WebClient client = WebClient.create();


	// Caching the results on class creation since we have no pagination in the requests to the NYCTimes service and it returns a limited amount of responses.
	// We collect all results at the application start and we manually paginate with the `getNews`.
	// TODO: implement retry logic
	private BbcNewsDto newsResponse;

	public BbcStrategy() {
		fetchNews().subscribe(response -> this.newsResponse = response);
	}

	private Mono<BbcNewsDto> fetchNews() {
		return client.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(BbcNewsDto.class)
				.map(response -> response)
				.onErrorResume(e -> Mono.error(new RuntimeException("Error fetching news from BBC", e)));
	}

	@Override()
	public News[] getNews(Pageable page) {
		if (this.newsResponse == null || !"ok".equals(this.newsResponse.getStatus())) {
			throw new NewsFetchException("ERROR_BBC");
		}

		int startIdx = page.getPageNumber() * page.getPageSize();
		int endIdx = Math.min(this.newsResponse.getTotalResults(), page.getPageSize() + startIdx);

		return IntStream.range(startIdx, endIdx)
				.mapToObj(i -> this.newsResponse.getArticles()[i])
				.map(article -> new News(article.getTitle(), article.getUrl(), article.getPublishedAt(), "", Source.Bbc))
				.toArray(News[]::new);
	}
}


class BbcNewsDto {
	private final String status;
	private final BbcNewsArticle[] articles;
	private final int totalResults;

	@JsonCreator
	public BbcNewsDto(
			@JsonProperty("status") String status,
			@JsonProperty("articles") BbcNewsArticle[] articles,
			@JsonProperty("totalResults") int totalResults) {
		this.status = status;
		this.articles = articles;
		this.totalResults = totalResults;
	}

	public String getStatus() {
		return status;
	}

	public BbcNewsArticle[] getArticles() {
		return articles;
	}

	public int getTotalResults() {
		return totalResults;
	}
}

class BbcNewsArticle {
	private final Source source;
	private final String author;
	private final String title;
	private final String description;
	private final String url;
	private final String urlToImage;
	private final Date publishedAt;
	private final String content;

	public BbcNewsArticle(
			@JsonProperty("source") Source source,
			@JsonProperty("author") String author,
			@JsonProperty("title") String title,
			@JsonProperty("description") String description,
			@JsonProperty("url") String url,
			@JsonProperty("urlToImage") String urlToImage,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
			@JsonProperty("publishedAt") Date publishedAt,
			@JsonProperty("content") String content) {
		this.source = source;
		this.author = author;
		this.title = title;
		this.description = description;
		this.url = url;
		this.urlToImage = urlToImage;
		this.publishedAt = publishedAt;
		this.content = content;
	}

	public Source getSource() {
		return source;
	}

	public String getAuthor() {
		return author;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public String getUrlToImage() {
		return urlToImage;
	}

	public Date getPublishedAt() {
		return publishedAt;
	}

	public String getContent() {
		return content;
	}


	static class Source {
		private String id;
		private String name;

		@JsonCreator
		public Source(@JsonProperty("id") String id, @JsonProperty("name") String name) {
			this.id = id;
			this.name = name;
		}

		// Getters
		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}
}
