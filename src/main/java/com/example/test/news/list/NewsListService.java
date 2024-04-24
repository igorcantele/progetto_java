package com.example.test.news.list;

import com.example.test.news.News;
import com.example.test.news.dto.CreateNewsDto;
import com.example.test.news.strategies.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class NewsListService {
	private final NewsListRepository repository;

	@Autowired()
	NewsListService(NewsListRepository repository) {
		this.repository = repository;
	}

	public void addNews(CreateNewsDto newsDto) {
		News news = new News(newsDto.getTitle(), newsDto.getUrl(), newsDto.getPublishDate(), newsDto.getType(), newsDto.getSource());
		repository.insert(news);
	}

	public List<News> getAll() {
		return repository.findAll();
	}

	public Page<News> getNewsByTitle(String title, int page, int size) {
		String pattern = ".*" + title + ".*";
		try {
			Pattern.compile(pattern);
			Pageable pageable = PageRequest.of(page, size);
			return repository.findByTitleLike(pattern, pageable);
		} catch (PatternSyntaxException e) {
			throw new BadRequestException("INVALID_TITLE");
		}
	}
}
