package com.ams.bankapp2.controller;

import com.ams.bankapp2.database.DatabaseConnection;
import com.ams.bankapp2.model.LogEntry;
import com.ams.bankapp2.model.User;
import com.ams.bankapp2.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final DatabaseConnection databaseConnection;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService, DatabaseConnection databaseConnection) {
        this.userService = userService;
        this.databaseConnection = databaseConnection;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "user-login"; // user-login.html Thymeleaf template
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.authenticateUser(username, password);
        if (user != null) {
            session.setAttribute("loggedUser", user);
            return "redirect:/users/dashboard";  // Make sure this redirection is correct
        } else {
            model.addAttribute("loginError", "Invalid username or password");
            return "user-login";
        }
    }


    @GetMapping("/signup")
    public String showSignupForm() {
        return "user-signup"; // user-signup.html Thymeleaf template
    }

    @PostMapping("/signup")
    public String processSignup(@RequestParam String username, @RequestParam String password, Model model, RedirectAttributes redirectAttributes) {
        boolean success = userService.registerNewUser(username, password);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Registration successful");
            return "redirect:/users/login";
        } else {
            model.addAttribute("signupError", "Registration failed. Username may already be taken.");
            return "user-signup";
        }
    }

    @GetMapping("/dashboard")
    public String userDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            model.addAttribute("user", user);
            return "user-dashboard"; // user-dashboard.html Thymeleaf template
        } else {
            return "redirect:/users/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    // Other methods for deposit, withdraw, transfer, etc.
    @GetMapping("/deposit")
    public String showDepositForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("user", user);
        return "deposit"; // deposit.html Thymeleaf template
    }

    @PostMapping("/deposit")
    public String processDeposit(@RequestParam double amount, HttpSession session, Model model, DatabaseConnection databaseConnection) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null && userService.depositToUserAccount(user.getId(), amount, user.getUsername())) {
            User updatedUser = userService.getUserById(user.getId());
            session.setAttribute("loggedUser", updatedUser);
            model.addAttribute("user", updatedUser);
            // Create and save a log entry for the deposit
            model.addAttribute("message", "Deposit successful");
        } else {
            model.addAttribute("error", "Deposit failed");
        }
        return "redirect:/users/dashboard";
    }

    @GetMapping("/withdraw")
    public String showWithdrawForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("user", user);
        return "withdraw"; // withdraw.html Thymeleaf template
    }

    @PostMapping("/withdraw")
    public String processWithdraw(@RequestParam double amount, @RequestParam String password, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null && userService.validatePassword(user.getId(), password)) {
            if (userService.withdrawFromUserAccount(user.getId(), amount, user.getUsername())) {
                User updatedUser = userService.getUserById(user.getId());
                session.setAttribute("loggedUser", updatedUser);
                model.addAttribute("user", updatedUser);
                model.addAttribute("message", "Withdrawal successful");
            } else {
                redirectAttributes.addFlashAttribute("error", "Insufficient funds or withdrawal failed");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid password");
        }
        return "redirect:/users/dashboard";
    }


    @GetMapping("/transfer")
    public String showTransferForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("user", user);
        return "transfer"; // transfer.html Thymeleaf template
    }

    @PostMapping("/transfer")
    public String processTransfer(@RequestParam String receiverUsername, @RequestParam double amount, @RequestParam String password, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null && userService.validatePassword(user.getId(), password)) {
            if (userService.transferBetweenUsers(user, receiverUsername, amount)) {
                User updatedUser = userService.getUserById(user.getId());
                session.setAttribute("loggedUser", updatedUser);
                model.addAttribute("user", updatedUser);
                model.addAttribute("message", "Transfer successful");
            } else {
                if (userService.checkIfUserExists(receiverUsername)){
                    redirectAttributes.addFlashAttribute("error", "Transfer failed or insufficient funds");
                }else {
                    redirectAttributes.addFlashAttribute("error", "Receiver does not exist");
                }
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid password");
        }
        return "redirect:/users/dashboard";
    }

    @PostMapping("/updatePassword")
    public String updateUserPassword(@RequestParam String currentPassword, @RequestParam String newPassword, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedUser");

        if (user != null && userService.validatePassword(user.getId(), currentPassword)) {
            // If current password is valid, proceed with updating the password
            boolean passwordUpdateSuccess = userService.updateUserPassword(user.getUsername(), newPassword);

            if (passwordUpdateSuccess) {
                // Invalidate the session to log the user out
                session.invalidate();

                // Add a message to inform the user to log in again
                redirectAttributes.addFlashAttribute("message", "Password updated successfully. Please log in again.");
                return "redirect:/users/login";
            } else {
                // Password update failed
                redirectAttributes.addFlashAttribute("error", "Failed to update password");
                return "redirect:/users/dashboard";
            }
        } else {
            // Current password validation failed
            redirectAttributes.addFlashAttribute("error", "Incorrect current password");
            return "redirect:/users/dashboard";
        }
    }

    @GetMapping("/delete")
    public String showDeleteConfirmation(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            model.addAttribute("user", user);
            return "delete-confirmation"; // delete-confirmation.html Thymeleaf template
        }
        return "redirect:/users/login";
    }

    @PostMapping("/delete")
    public String deleteUserAccount(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            boolean deletionSuccess = userService.deleteUser(user.getUsername());
            session.invalidate();

            if (deletionSuccess) {
                redirectAttributes.addFlashAttribute("message", "Account deleted successfully.");
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to delete account.");
                return "redirect:/users/dashboard";
            }
        }
        return "redirect:/users/login";
    }

    @GetMapping("/log")
    public String showUserLog(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            List<LogEntry> logEntries = userService.getLogEntriesByUsername(user.getUsername());
            model.addAttribute("user", user);
            model.addAttribute("logEntries", logEntries);
            return "user-log"; // user-log.html Thymeleaf template
        }
        return "redirect:/users/login";
    }
}