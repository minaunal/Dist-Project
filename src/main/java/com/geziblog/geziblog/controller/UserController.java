package com.geziblog.geziblog.controller;
import com.geziblog.geziblog.controller.dto.*;
import com.geziblog.geziblog.entity.User;
import com.geziblog.geziblog.service.AuthenticationService;
import com.geziblog.geziblog.entity.Post;
import com.geziblog.geziblog.service.JwtService;
import com.geziblog.geziblog.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;




    @PostMapping("/login/save")
    public void save(@ModelAttribute("userDto") UserDTO userDto, Model model) {
        authenticationService.save(userDto);
        model.addAttribute("message", "Submitted Successfully");
    }

    @PostMapping("/login/auth")
    public UserResponse auth(@ModelAttribute("userRequest") UserRequest userRequest, Model model) {
       return authenticationService.auth(userRequest);
    }

    @PostMapping("/login/finduser")
    public findDTO findUser(@RequestHeader("Authorization") String bearerToken,@ModelAttribute("finduser") findDTO findDTO, Model model){
        String token = bearerToken.substring(7);
        String username = jwtService.findUsername(token);
        findDTO found=userService.ifFollows(userService.getUser(username),findDTO);
        return found;
    }
    @PostMapping("/login/findPost")
    public List<Anasayfa> findPost(@RequestHeader("Authorization") String bearerToken, @ModelAttribute("findpost") findDTO findDTO, Model model){
        String token = bearerToken.substring(7);
        String username = jwtService.findUsername(token);
        List<Post>posts= userService.findPosts(findDTO.getUsername(),findDTO.getUsername());
        List<Anasayfa> anasayfa=new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            Anasayfa anasayfaItem = new Anasayfa();
            anasayfaItem.setUsername(userService.findUserbyId(post.getUser().getId()).getUsername());
            anasayfaItem.setBaslik(post.getBaslik());
            anasayfaItem.setMetin(post.getMetin());
            anasayfa.add(anasayfaItem);
        }
return anasayfa;
    }
    @PostMapping("/login/password")
    public void updatePassword(@ModelAttribute("userreq") UserRequest userRequest,Model model) {
        authenticationService.updatePassword(userRequest);
    }
    @PostMapping("/login/follow")
    public List<Anasayfa> followUser(@RequestHeader("Authorization") String authorizationHeader, @RequestBody String username, Model model){
        String token = authorizationHeader.substring(7);
        String myusername = jwtService.findUsername(token);
        userService.followUser(myusername, username);
        List<Post> posts= userService.getPosts(userService.getUser(username).getId());
        List<Anasayfa> anasayfa=new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            Anasayfa anasayfaItem = new Anasayfa();
            anasayfaItem.setUsername(userService.findUserbyId(post.getUser().getId()).getUsername());
            anasayfaItem.setBaslik(post.getBaslik());
            anasayfaItem.setMetin(post.getMetin());
            anasayfa.add(anasayfaItem);
        }
        return anasayfa;
    }

    @PostMapping("/login/savepost")
    public void savePost(@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute("mypost") Post post,Model model){
            String token = authorizationHeader.substring(7);
            String username = jwtService.findUsername(token);
            userService.savePostToUser(post, username);
    }
    @PostMapping("/login/anasayfa")
    public List<Anasayfa> homePage(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.substring(7);
        String username = jwtService.findUsername(token);
        User currentuser=userService.getUser(username);
        List<Post>posts= userService.followingsPosts(currentuser.getId());
        List<Anasayfa> anasayfa=new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            Anasayfa anasayfaItem = new Anasayfa();
            anasayfaItem.setUsername(userService.findUserbyId(post.getUser().getId()).getUsername());
            anasayfaItem.setBaslik(post.getBaslik());
            anasayfaItem.setMetin(post.getMetin());
            anasayfa.add(anasayfaItem);
        }
        return anasayfa;
    }
    @PostMapping("/logout/")
    public void logout(@RequestHeader("Authorization") String authorizationHeader){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
    }


}