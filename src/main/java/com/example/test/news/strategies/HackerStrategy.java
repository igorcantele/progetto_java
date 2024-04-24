package com.example.test.news.strategies;

import com.example.test.news.News;
import com.example.test.news.Source;
import com.example.test.news.strategies.exceptions.NewsFetchException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;

@Component()
public class HackerStrategy implements NewsStrategy {
	private final String idxUri = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
	private final String detailUri = "https://hacker-news.firebaseio.com/v0/item/[id_news].json?print=pretty";
	private final WebClient client = WebClient.create();


	// Caching the results on class creation since we have no pagination in the requests to the NYCTimes service and it returns a limited amount of responses.
	// We collect all results at the application start and we manually paginate with the `getNews`.
	// TODO: implement retry logic
	private News[] news;

	public HackerStrategy() {
		fetchNews().subscribe(newsArray -> this.news = newsArray);
	}

	private Mono<News[]> fetchNews() {
		return client.get()
				.uri(idxUri)
				.retrieve()
				.bodyToMono(int[].class)
				.flatMapMany(ids -> Flux.fromArray(Arrays.stream(ids)
						.boxed()
						.toArray(Integer[]::new)))
				.flatMap(this::getDetails)
				.collectList()
				.map(list -> list.toArray(new News[0]));
	}


	private Mono<News> getDetails(int idx) {
		return client.get()
				.uri(getDetailUri(idx))
				.retrieve()
				.bodyToMono(HackerNewsArticle.class)
				.onErrorResume(e -> Mono.error(new RuntimeException("Error fetching news from Hacker", e)))
				.map(hackerNewsArticle -> new News(hackerNewsArticle.getTitle(), hackerNewsArticle.getUrl(), hackerNewsArticle.getTime(), hackerNewsArticle.getType(), Source.Hacker));
	}


	private String getDetailUri(int idx) {
		String uri = this.detailUri.replace("[id_news]", String.valueOf(idx));
		return uri;
	}

	@Override()
	public News[] getNews(Pageable page) {
		if (this.news == null) {
			throw new NewsFetchException("ERROR_HACKER");
		}
		int startIdx = page.getPageNumber() * page.getPageSize();
		int endIdx = Math.min(this.news.length, page.getPageSize() + startIdx);

		return Arrays.copyOfRange(this.news, startIdx, endIdx);
	}
}


class HackerNewsArticle {
	private final String by;
	private final int descendants;
	private final long id;
	private final long[] kids;
	private final int score;
	private final Date time;
	private final String title;
	private final String type;
	private final String url;

	@JsonCreator
	public HackerNewsArticle(@JsonProperty("by") String by,
	                         @JsonProperty("descendants") int descendants,
	                         @JsonProperty("id") long id,
	                         @JsonProperty("kids") long[] kids,
	                         @JsonProperty("score") int score,
	                         @JsonProperty("time") long time,
	                         @JsonProperty("title") String title,
	                         @JsonProperty("type") String type,
	                         @JsonProperty("url") String url) {
		this.by = by;
		this.descendants = descendants;
		this.id = id;
		this.kids = kids;
		this.score = score;
		this.time = new Date(time * 1000);
		this.title = title;
		this.type = type;
		this.url = url;
	}

	public String getBy() {
		return by;
	}

	public int getDescendants() {
		return descendants;
	}

	public long getId() {
		return id;
	}

	public long[] getKids() {
		return kids;
	}

	public int getScore() {
		return score;
	}

	public Date getTime() {
		return time;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}
}
