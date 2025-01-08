package com.geziblog.geziblog.controller;
import com.geziblog.geziblog.controller.dto.*;
import com.geziblog.geziblog.entity.Place;
import com.geziblog.geziblog.entity.User;
import com.geziblog.geziblog.service.AuthenticationService;
import com.geziblog.geziblog.entity.Post;
import com.geziblog.geziblog.service.JwtService;
import com.geziblog.geziblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import java.util.*;

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

    //KULLANICI İŞLEMLERİ
    @PostMapping("/save/user")
    public void save(@RequestBody UserDTO userDto) {
        authenticationService.save(userDto);
    }
    @PostMapping("/login")
    public UserResponse auth(@RequestBody UserRequest userRequest) {
       return authenticationService.auth(userRequest);
    }
    @GetMapping("/signout")
    public void logout(@RequestHeader("Authorization") String authorizationHeader){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
    }
    @PostMapping("/updatePassword")
    public void updatePassword(@RequestHeader("Authorization") String authorizationHeader, @RequestBody UserRequest userRequest) {
        authenticationService.updatePassword(userRequest);
    }
    @PostMapping("/follow") //body format: text örn: senaelmas
    public List<Anasayfa> followUser(@RequestHeader("Authorization") String authorizationHeader, @RequestBody String username){
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
            anasayfaItem.setRota(post.getPlace().getPlaces());
            anasayfa.add(anasayfaItem);
        }
        return anasayfa;
    }
    @PostMapping("/findUser")
    public findDTO findUser(@RequestHeader("Authorization") String bearerToken,@RequestBody findDTO findDTO){
        String token = bearerToken.substring(7);
        String username = jwtService.findUsername(token);
        findDTO found=userService.ifFollows(userService.getUser(username),findDTO);
        return found;
    }

    //POST İŞLEMLERİ
    @PostMapping("/findPost")
    public List<Anasayfa> findPost(@RequestHeader("Authorization") String bearerToken, @RequestBody findDTO findDTO){
        String token = bearerToken.substring(7);
        String username = jwtService.findUsername(token);
        List<Post>posts= userService.findPosts(findDTO.getKeyword()); //username hem kişi hem de aranacak kelime için
        List<Anasayfa> anasayfa=new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            Anasayfa anasayfaItem = new Anasayfa();
            anasayfaItem.setUsername(userService.findUserbyId(post.getUser().getId()).getUsername());
            anasayfaItem.setBaslik(post.getBaslik());
            anasayfaItem.setMetin(post.getMetin());
            anasayfaItem.setRota(post.getPlace().getPlaces());
            anasayfa.add(anasayfaItem);
        }
    return anasayfa;
    }
    @PostMapping("/sharePost")
    public void sharePost(@RequestHeader("Authorization") String authorizationHeader,@RequestParam("place_id") int placeId,@RequestBody postDTO postDTO) {
            String token = authorizationHeader.substring(7);
            String username = jwtService.findUsername(token);
            User user=userService.getUser(username);
            Post post=new Post();
            Place place=new Place();
            place.setId(placeId);
            place.setUser(user);
            place.setPlaces(userService.getPlaceNamesFromPlace_id(placeId));
            post.setPlace(place);
            post.setMetin(postDTO.getMetin());
            post.setBaslik(postDTO.getBaslik());
            userService.savePostToUser(post,username);
    }
    @DeleteMapping("/deletePost")
    public void deletePost(@RequestHeader("Authorization") String authorizationHeader, @RequestParam("post_id") Long post_id){
        String token = authorizationHeader.substring(7);
        String username = jwtService.findUsername(token);
        userService.deletePost(post_id);
    }
    @GetMapping("/homepage")
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
            anasayfaItem.setRota(post.getPlace().getPlaces());
            anasayfa.add(anasayfaItem);
        }
        return anasayfa;
    }

    //MAPS İŞLEMLERİ
    @GetMapping("/places")
    public ResponseEntity<List<String>> getSuggestions(@RequestHeader("Authorization") String bearerToken,@RequestParam String input,@RequestParam String arrivalDate, @RequestParam String departureDate) {
        try {
            // Authorization başlığından token'ı çıkar
            String token = bearerToken.substring(7);
            String username = jwtService.findUsername(token);

            // UserService metodu çağrılır
            List<String> topPlaceIds = userService.getTopRatedPlaces(input, arrivalDate, departureDate);

            // Başarılı yanıt döndür
            return ResponseEntity.ok(topPlaceIds);
        } catch (Exception e) {
            // Hata durumunda HTTP 500 döndür
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList("Hata: " + e.getMessage()));
        }
    }
    @GetMapping("/saveRoute") //önerdiği rotayı O AN kaydetmek istersem
    public void saveSuggestion(@RequestHeader("Authorization") String bearerToken){
        String token = bearerToken.substring(7);
        String username = jwtService.findUsername(token);
        User user=userService.getUser(username);
        Place newplace=new Place();
        newplace.setUser(user);
        newplace.setPlaces(userService.getPlaceNames()); //o an yerlerin ismi kaydediliyor
        userService.savePlace(newplace);
    }
    @GetMapping("/saved")
    public List<String> loadSaved(@RequestHeader("Authorization") String bearerToken){
        String token = bearerToken.substring(7);
        String username = jwtService.findUsername(token);
        return userService.getSavedPlaces(username);
    }

}