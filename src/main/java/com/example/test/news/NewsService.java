package com.example.test.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service()
public class NewsService {
	private final NewsStrategyService newsService;

	@Autowired()
	NewsService(NewsStrategyService newsService) {
		this.newsService = newsService;
	}

	News[] getByAllSources(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return this.newsService.useAll(pageable);
	}

	News[] getByStrategy(String strategyName, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return this.newsService.use(strategyName, pageable);
	}
}
