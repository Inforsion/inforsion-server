package com.inforsion.inforsionserver.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@GetMapping
	public String userForm() {
		return "user";
	}
	@PostMapping
	public String saveUser(UserDTO userDto) {
		userService.saveUser(userDto);
		return "redirect:/";
	}
}
