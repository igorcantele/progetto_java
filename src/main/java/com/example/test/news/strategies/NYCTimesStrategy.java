package com.example.test.news.strategies;

import com.example.test.news.News;
import com.example.test.news.Source;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;


@Component()
public class NYCTimesStrategy implements NewsStrategy {
	private final String uri = "https://api.nytimes.com/svc/mostpopular/v2/emailed/7.json?api-key=P9eZP8Gn1gllxx3q5QDlsJwsmQ1yQgAN";
	private final WebClient client = WebClient.create();


	// Caching the results on class creation since we have no pagination in the requests to the NYCTimes service and it returns a limited amount of responses.
	// We collect all results at the application start and we manually paginate with the `getNews`.
	// TODO: implement retry logic
	private NYCNewsDto newsResponse;

	public NYCTimesStrategy() {
		fetchNews().subscribe(response -> this.newsResponse = response);
	}

	private Mono<NYCNewsDto> fetchNews() {
		return client.get()
				.uri(uri)
				.retrieve()
				.bodyToMono(NYCNewsDto.class)
				.onErrorResume(e -> Mono.error(new RuntimeException("Error fetching news from NYTimes", e)));
	}

	@Override
	public News[] getNews(Pageable page) {
		if (this.newsResponse == null || !"OK".equals(this.newsResponse.getStatus())) {
			throw new RuntimeException("ERROR_NYTIMES");
		}

		int startIdx = page.getPageNumber() * page.getPageSize();
		int endIdx = Math.min(this.newsResponse.getNum_results(), page.getPageSize() + startIdx);

		return IntStream.range(startIdx, endIdx)
				.mapToObj(i -> this.newsResponse.getResults()[i])
				.map(article -> new News(article.getTitle(), article.getUrl(), article.getPublishedDate(), "", Source.NyTimes))
				.toArray(News[]::new);
	}

}

class NYCNewsDto {
	private final String status;
	private final String copyright;
	private final NYCNewsArticle[] results;
	private final int num_results;

	@JsonCreator
	public NYCNewsDto(@JsonProperty("status") String status,
	                  @JsonProperty("copyright") String copyright,
	                  @JsonProperty("results") NYCNewsArticle[] results,
	                  @JsonProperty("num_results") int num_results) {
		this.status = status;
		this.copyright = copyright;
		this.results = results;
		this.num_results = num_results;
	}

	public String getStatus() {
		return status;
	}

	public String getCopyright() {
		return copyright;
	}

	public NYCNewsArticle[] getResults() {
		return results;
	}

	public int getNum_results() {
		return num_results;
	}
}

class NYCNewsArticle {
	private String uri;
	private String url;
	private long id;
	private long assetId;
	private String source;
	private Date publishedDate;
	private Date updated;
	private String section;
	private String subsection;
	private String nytdSection;
	private String adxKeywords;
	private String column;
	private String byline;
	private String type;
	private String title;
	private String abstractText;
	private List<String> desFacet;
	private List<String> orgFacet;
	private List<String> perFacet;
	private List<String> geoFacet;
	private List<Media> media;
	private long etaId;

	@JsonCreator
	public NYCNewsArticle(
			@JsonProperty("uri") String uri,
			@JsonProperty("url") String url,
			@JsonProperty("id") long id,
			@JsonProperty("asset_id") long assetId,
			@JsonProperty("source") String source,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
			@JsonProperty("published_date") Date publishedDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
			@JsonProperty("updated") Date updated,
			@JsonProperty("section") String section,
			@JsonProperty("subsection") String subsection,
			@JsonProperty("nytd_section") String nytdSection,
			@JsonProperty("adx_keywords") String adxKeywords,
			@JsonProperty("column") String column,
			@JsonProperty("byline") String byline,
			@JsonProperty("type") String type,
			@JsonProperty("title") String title,
			@JsonProperty("abstract") String abstractText,
			@JsonProperty("des_facet") List<String> desFacet,
			@JsonProperty("org_facet") List<String> orgFacet,
			@JsonProperty("per_facet") List<String> perFacet,
			@JsonProperty("geo_facet") List<String> geoFacet,
			@JsonProperty("media") List<Media> media,
			@JsonProperty("eta_id") long etaId) {
		this.uri = uri;
		this.url = url;
		this.id = id;
		this.assetId = assetId;
		this.source = source;
		this.publishedDate = publishedDate;
		this.updated = updated;
		this.section = section;
		this.subsection = subsection;
		this.nytdSection = nytdSection;
		this.adxKeywords = adxKeywords;
		this.column = column;
		this.byline = byline;
		this.type = type;
		this.title = title;
		this.abstractText = abstractText;
		this.desFacet = desFacet;
		this.orgFacet = orgFacet;
		this.perFacet = perFacet;
		this.geoFacet = geoFacet;
		this.media = media;
		this.etaId = etaId;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public static class Media {
		private String type;
		private String subtype;
		private String caption;
		private String copyright;
		private int approvedForSyndication;
		private List<MediaMetadata> mediaMetadata;

		@JsonCreator
		public Media(
				@JsonProperty("type") String type,
				@JsonProperty("subtype") String subtype,
				@JsonProperty("caption") String caption,
				@JsonProperty("copyright") String copyright,
				@JsonProperty("approved_for_syndication") int approvedForSyndication,
				@JsonProperty("media-metadata") List<MediaMetadata> mediaMetadata) {
			this.type = type;
			this.subtype = subtype;
			this.caption = caption;
			this.copyright = copyright;
			this.approvedForSyndication = approvedForSyndication;
			this.mediaMetadata = mediaMetadata;
		}

		public static class MediaMetadata {
			private String url;
			private String format;
			private int height;
			private int width;

			@JsonCreator
			public MediaMetadata(
					@JsonProperty("url") String url,
					@JsonProperty("format") String format,
					@JsonProperty("height") int height,
					@JsonProperty("width") int width) {
				this.url = url;
				this.format = format;
				this.height = height;
				this.width = width;
			}
		}
	}
}
