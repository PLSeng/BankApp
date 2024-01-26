package com.ams.bankapp2.controller;

import com.ams.bankapp2.database.DatabaseConnection;
import com.ams.bankapp2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DatabaseConnection databaseConnection;
    private final UserService userService;

    @Autowired
    public AdminController(DatabaseConnection databaseConnection, UserService userService) {
        this.databaseConnection = databaseConnection;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showAdminLoginForm() {
        return "admin-login"; // admin-login.html Thymeleaf template
    }

    @PostMapping("/login")
    public String handleAdminLogin(@RequestParam String username, @RequestParam String password, Model model) {
        // Assume admin credentials are predefined or configured
        if ("admin".equals(username) && "admin".equals(password)) {
            return "redirect:/admin/dashboard";
        } else {
            model.addAttribute("loginError", "Invalid admin credentials");
            return "admin-login";
        }
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard"; // admin-dashboard.html Thymeleaf template
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", databaseConnection.getAllUsers());
        return "admin-users"; // admin-users.html Thymeleaf template
    }

    @GetMapping("/addUser1")
    public String showAddUserForm() {
        return "add-new-user";
    }

    @PostMapping("/addUser1")
    public String addUser(@RequestParam String username, @RequestParam String password, Model model) {
        boolean success = databaseConnection.addUser(username, password);
        if (success) {
            model.addAttribute("message", "User added successfully");
        } else {
            model.addAttribute("error", "Failed to add user");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam String username, Model model) {
        boolean success = userService.deleteUser(username);
        if (success) {
            model.addAttribute("message", "User deleted successfully");
        } else {
            model.addAttribute("error", "Failed to delete user");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/updatePassword1")
    public String showUpdatePasswordForm() {
        return "update-user-password";
    }

    @PostMapping("/updatePassword1")
    public String updateUserPassword(@RequestParam String username, @RequestParam String newPassword, Model model) {
        boolean success = databaseConnection.updateUserPassword(username, newPassword);
        if (success) {
            model.addAttribute("message", "User password updated successfully");
        } else {
            model.addAttribute("error", "Failed to update user password");
        }
        return "redirect:/admin/users";
    }
}
