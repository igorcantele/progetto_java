package com.example.test.news.list;

import com.example.test.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface NewsListRepository extends MongoRepository<News, String> {
	@Query("{'title': {$regex: ?0, $options: 'i'}}")
	Page<News> findByTitleLike(String title, Pageable pageable);
}
