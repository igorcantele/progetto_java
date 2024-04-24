package com.example.test.news.strategies.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
public class NewsFetchException extends RuntimeException {
	public NewsFetchException(String message) {
		super(message);
	}
}
