package com.example.deepstart.controller;


import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.deepstart.model.UserEntity;
import com.example.deepstart.model.UserForm;
import com.example.deepstart.repository.UserRepository;


@Controller
public class FormController {
	private final UserRepository userRepository;
	
	public FormController(UserRepository userRepository) {
	    this.userRepository = userRepository;
	}
	
    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "form";
    }

    @PostMapping("/confirm")
    public String confirmForm(@ModelAttribute @Valid  UserForm userForm,
            BindingResult bindingResult, Model model) {
        model.addAttribute("userForm", userForm);
        if (bindingResult.hasErrors()) {
            return "form"; // エラーがあればformに戻る
        }
        return "confirm";
    }
    @PostMapping("/result")
    public String result(@ModelAttribute UserForm userForm, Model model) {
        // 保存処理
        UserEntity user = new UserEntity();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        userRepository.save(user);
        
        model.addAttribute("userForm", userForm);
        return "result";
    }
    @GetMapping("/list")
    public String userList(Model model) {
        List<UserEntity> userList = userRepository.findAll();
        model.addAttribute("userList", userList);
        return "list";
    }
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        UserEntity user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "edit";
    }
    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserEntity user) {
        userRepository.save(user); // 上書き保存
        return "redirect:/list";
    }
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/list";
    }
}
