package com.example.test.news.list;

import com.example.test.news.News;
import com.example.test.news.dto.CreateNewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller()
@Validated
@RequestMapping("/list")
public class NewsListController {
	private final NewsListService service;

	@Autowired()
	NewsListController(NewsListService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity addExpense(@Valid @RequestBody CreateNewsDto expense) {
		service.addNews(expense);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<List<News>> getAll() {
		return ResponseEntity.ok(service.getAll());
	}

	@GetMapping("/{title}")
	public ResponseEntity<Page<News>> getByTitle(
			@PathVariable String title,
			@RequestParam(value = "page", defaultValue = "0", required = false) int page,
			@RequestParam(value = "size", defaultValue = "10", required = false) int size) {
		return ResponseEntity.ok(service.getNewsByTitle(title, page, size));
	}
}
