package com.example.test.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
@RequestMapping("/news")
public class NewsController {
	private final NewsService service;

	@Autowired()
	NewsController(NewsService service) {
		this.service = service;
	}

	@GetMapping("")
	public ResponseEntity<News[]> getMany(
			@RequestParam(value = "page", defaultValue = "0", required = false) int page,
			@RequestParam(value = "size", defaultValue = "10", required = false) int size) {
		News[] news = this.service.getByAllSources(page, size);
		return ResponseEntity.ok(news);
	}

	@GetMapping("/{strategy}")
	public ResponseEntity<News[]> getManyWithStrategies(
			@PathVariable String strategy,
			@RequestParam(value = "page", defaultValue = "0", required = false) int page,
			@RequestParam(value = "size", defaultValue = "10", required = false) int size) {
		News[] news = this.service.getByStrategy(strategy, page, size);
		return ResponseEntity.ok(news);
	}
}
