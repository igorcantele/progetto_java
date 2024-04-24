package com.example.test.news;

import com.example.test.news.strategies.BbcStrategy;
import com.example.test.news.strategies.HackerStrategy;
import com.example.test.news.strategies.NYCTimesStrategy;
import com.example.test.news.strategies.NewsStrategy;
import com.example.test.news.strategies.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.Map.entry;

@Service()
public class NewsStrategyService {
	private final BbcStrategy bbcStrategy;
	private final HackerStrategy hackerStrategy;
	private final NYCTimesStrategy nycTimesStrategy;
	private final Map<Source, NewsStrategy> sourceToStrategy;

	@Autowired()
	public NewsStrategyService(BbcStrategy bbcStrategy, HackerStrategy hackerStrategy, NYCTimesStrategy nycTimesStrategy) {
		this.bbcStrategy = bbcStrategy;
		this.hackerStrategy = hackerStrategy;
		this.nycTimesStrategy = nycTimesStrategy;
		this.sourceToStrategy = Map.ofEntries(
				entry(Source.NyTimes, this.nycTimesStrategy),
				entry(Source.Bbc, this.bbcStrategy),
				entry(Source.Hacker, this.hackerStrategy)
		);
	}

	public News[] use(String source, Pageable page) {
		Source strategyName = this.validateStrategy(source);
		NewsStrategy strategy = this.sourceToStrategy.get(strategyName);
		return strategy.getNews(page);
	}

	public News[] useAll(Pageable page) {
		int size = Math.floorDiv(page.getPageSize(), this.sourceToStrategy.size());
		Stream<News> news = Stream.empty();

		for (var strategy : this.sourceToStrategy.values()) {
			Pageable pageable = PageRequest.of(page.getPageNumber(), size);
			News[] newsArray = strategy.getNews(pageable);
			news = Stream.concat(news, Stream.of(newsArray));
		}

		return news.toArray(News[]::new);
	}

	private Source validateStrategy(String strategyName) {
		if (!ObjectUtils.containsConstant(Source.values(), strategyName, true) || !this.sourceToStrategy.containsKey(Source.valueOf(strategyName))) {
			throw new BadRequestException("INVALID_STRATEGY");
		}
		return Source.valueOf(strategyName);
	}

}
