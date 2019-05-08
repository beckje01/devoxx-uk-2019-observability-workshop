package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/users"})
@Slf4j
public class UserController {

	private UserRepository repository;

	UserController(UserRepository userRepository) {
		this.repository = userRepository;
	}


	@GetMapping
	public List findAll() {
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
	public User create(@RequestBody User user){
		return repository.save(user);
	}
}





