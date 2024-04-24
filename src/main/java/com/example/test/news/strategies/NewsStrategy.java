package com.example.test.news.strategies;

import com.example.test.news.News;
import org.springframework.data.domain.Pageable;

public interface NewsStrategy {
	public News[] getNews(Pageable page);
}
