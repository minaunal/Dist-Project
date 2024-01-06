package com.geziblog.geziblog.controller;
import com.geziblog.geziblog.controller.dto.findDTO;
import com.geziblog.geziblog.service.AuthenticationService;
import com.geziblog.geziblog.controller.dto.UserDTO;
import com.geziblog.geziblog.controller.dto.UserResponse;
import com.geziblog.geziblog.controller.dto.UserRequest;
import com.geziblog.geziblog.entity.Post;
import com.geziblog.geziblog.service.JwtService;
import com.geziblog.geziblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

@RestController
@RequestMapping("/login")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;


    @PostMapping("/save")
    public void save(@ModelAttribute("userDto") UserDTO userDto, Model model) {
        authenticationService.save(userDto);
        model.addAttribute("message", "Submitted Successfully");
    }

    @PostMapping("/auth")
    public UserResponse auth(@ModelAttribute("userRequest") UserRequest userRequest, Model model) {
       return authenticationService.auth(userRequest);
    }

    @PostMapping("/find")
    public String findUser(@RequestHeader("Authorization") String bearerToken,@ModelAttribute("findDTO")findDTO findDTO, Model model){
        String token = bearerToken.substring(7);
        String username = jwtService.findUsername(token);
        return userService.returnUser(findDTO.getUsername());
    }
    @PostMapping("/follow/{id}")
    public void followUser(@PathVariable Integer id){
         userService.followUser(id);
    }
    @PostMapping("/savepost")
    public void savePost(@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute("mypost")Post post,Model model){
        String token = authorizationHeader.substring(7);
        String username = jwtService.findUsername(token);
        userService.savePostToUser(post, username);
    }

}