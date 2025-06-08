package com.example.deepstart.controller;


import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String userList(
    	    @RequestParam(name = "nameKeyword", required = false) String nameKeyword,
    	    @RequestParam(name = "emailKeyword", required = false) String emailKeyword,
    	    Model model) {
    	
        	List<UserEntity> userList;
        
            if ((nameKeyword != null && !nameKeyword.isBlank()) || (emailKeyword != null && !emailKeyword.isBlank())) {
                userList = userRepository.findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(
                    nameKeyword != null ? nameKeyword : "",
                    emailKeyword != null ? emailKeyword : ""
                );
            } else {
                userList = userRepository.findAll();
            }
        model.addAttribute("userList", userList);
        model.addAttribute("nameKeyword", nameKeyword);
        model.addAttribute("emailKeyword", emailKeyword);
        return "list";
    }
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            // IDが存在しない場合の処理（404など）
            return "redirect:/list";
        }
         model.addAttribute("user",userOpt.get());
        return "edit";
    }
    @PostMapping("/update")
    public String updateUser(
    		@ModelAttribute("user") @Valid UserEntity user, 
    		BindingResult bindingResult, 
    		Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "edit";
        }
        // 🔽 登録済みのcreatedAtを保持してから保存
        UserEntity original = userRepository.findById(user.getId()).orElseThrow();
        user.setCreatedAt(original.getCreatedAt());

    	userRepository.save(user); // 上書き保存
        return "redirect:/list";
    }
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/list";
    }
    @GetMapping("/export")
    public void exportCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");

        List<UserEntity> users = userRepository.findAll();

        PrintWriter writer = response.getWriter();
        writer.println("ID,名前,メール,登録日時,更新日時");

        for (UserEntity user : users) {
            writer.printf(
                "%d,%s,%s,%s,%s\n",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : "",
                user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : ""
            );
        }

        writer.flush();
        writer.close();
    }
}
