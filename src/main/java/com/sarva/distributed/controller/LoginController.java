package com.sarva.distributed.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.sarva.distributed.repository.UserRepository;
import com.sarva.distributed.services.BasketballMatchService;
import com.sarva.distributed.services.EmailService;
import com.sarva.distributed.services.SportsMatchService;
import com.sarva.distributed.models.User;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SportsMatchService sportsMatchService;

    @Autowired
    private BasketballMatchService basketballMatchService;

    @PostMapping("/submitPreferences")
    public String submitPreferences(@RequestParam(name = "sports", required = false) List<String> sports,
                                    @RequestParam("fromDate") String fromDate,
                                    @RequestParam("toDate") String toDate,
                                    HttpSession session) {
        
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (sports == null || sports.isEmpty()) {
            return "home";
        }

        if (sports.contains("Football") && sports.contains("Basketball")) {
            return "redirect:/combined_result?fromDate=" + fromDate + "&toDate=" + toDate;
        } else if (sports.contains("Football")) {
            return "redirect:/football_result?fromDate=" + fromDate + "&toDate=" + toDate;
        } else {
            return "redirect:/basketball_result?fromDate=" + fromDate + "&toDate=" + toDate;
        }
    }

    @GetMapping("/basketball_result")
    public String basketballResult(@RequestParam("fromDate") String fromDate,
                                   @RequestParam("toDate") String toDate,
                                   HttpSession session) {
        
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<String> matches = basketballMatchService.getBasketballMatches(fromDate, toDate);
        emailService.sendMatchEmailToUser(user.getEmail(), matches);

        return "basketball_result";
    }

    @GetMapping("/football_result")
    public String footballResult(@RequestParam("fromDate") String fromDate,
                                 @RequestParam("toDate") String toDate,
                                 HttpSession session) {
        
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        List<String> matches = sportsMatchService.getMatchesForUser(fromDate, toDate);
        emailService.sendMatchEmailToUser(user.getEmail(), matches);

        return "football_result";
    }

    @GetMapping("/combined_result")
    public String combinedResult(@RequestParam("fromDate") String fromDate,
                                 @RequestParam("toDate") String toDate,
                                 HttpSession session) {
        
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
    
        List<String> footballMatches = sportsMatchService.getMatchesForUser(fromDate, toDate);
        List<String> basketballMatches = basketballMatchService.getBasketballMatches(fromDate, toDate);
    
        StringBuilder emailContent = new StringBuilder();
    
        if (!footballMatches.isEmpty()) {
            emailContent.append("Football:\n");
            for (String match : footballMatches) {
                emailContent.append("- ").append(match).append("\n");
            }
            emailContent.append("\n");
        }
    
        if (!basketballMatches.isEmpty()) {
            emailContent.append("Basketball:\n");
            for (String match : basketballMatches) {
                emailContent.append("- ").append(match).append("\n");
            }
        }
    
        emailService.sendMatchEmailToUser(user.getEmail(), List.of(emailContent.toString()));
    
        return "combined_result";
    }



    @GetMapping("/")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User()); // Create an empty user object
        return "login"; // Show login.html
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, Model model, HttpSession session) { // Added HttpSession
        System.out.println("User entered: " + user.getName() + ", Password: " + user.getPassword());

        Optional<User> userdata = userRepository.findByName(user.getName());

        if (userdata.isPresent()) {
            User foundUser = userdata.get();
            System.out.println("User found: " + foundUser.getName());

            if (foundUser.getPassword().equals(user.getPassword())) {
                System.out.println("Login successful!");

                // Store the logged-in user in session
                session.setAttribute("loggedInUser", foundUser); // ✅ Set user in session

                return "home"; 
            } else {
                System.out.println("Incorrect password!");
                model.addAttribute("error", "Invalid username or password.");
            }
        } else {
            System.out.println("User not found!");
            model.addAttribute("error", "Invalid username or password.");
        }

        return "login"; // Stay on login page and show error
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration"; // Return registration.html
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("password") String password,
                               @RequestParam("email") String email,                           
                               Model model) {

        if (userRepository.findByName(name).isPresent()) {
            model.addAttribute("error", "Username already exists. Choose another.");
            return "registration"; // Stay on registration page if username exists
        }

       

        // Create and save user
        User newUser = new User(name, password, email);
        userRepository.save(newUser);

        return "redirect:/"; // Redirect to login page after successful registration
    }
}
