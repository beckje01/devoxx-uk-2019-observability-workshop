package com.example.demo;


import brave.Span;
import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/users"})
@Slf4j
public class UserController {

	private Tracer tracer;
	private UserRepository repository;

	UserController(UserRepository userRepository, Tracer tracer) {
		this.repository = userRepository;
		this.tracer = tracer;
	}


	@GetMapping
	public List findAll() {
		log.debug("Finding All");
		return repository.findAll();
	}

	@GetMapping(path = {"/{id}"})
	public ResponseEntity<User> findById(@PathVariable String id) {
		return repository.findById(id)
			.map(record -> {
				log.info(record.getUsername());
				return ResponseEntity.ok().body(record);
			})
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public User create(@RequestBody User user) {
		log.info("Saving a user.");
		Span span = tracer.nextSpan().name("saveUser");
		try (Tracer.SpanInScope ws = tracer.withSpanInScope(span.start())) {
			span.tag("username", user.getUsername());
			return repository.save(user);
		} finally {
			span.finish();
		}

	}


}





