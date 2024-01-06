package com.geziblog.geziblog.service;
import com.geziblog.geziblog.controller.dto.findDTO;
import com.geziblog.geziblog.controller.repository.FollowRepository;
import com.geziblog.geziblog.controller.repository.PostRepository;
import com.geziblog.geziblog.controller.repository.UserRepository;
import com.geziblog.geziblog.entity.Following;
import com.geziblog.geziblog.entity.Post;
import com.geziblog.geziblog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private JwtService jwtService;

    public void followUser(Integer id) {
        User follower=userRepository.findByMail("mina@gmail.com");
        User following=userRepository.findById(id);
        Following follow = new Following();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
    }

    public Post savePostToUser(Post post, String username) {
        post.setUser(userRepository.findByUsername(username).get()); //frontend yazılınca hesap bilgilerinden email cekilerek yapılacak
        return postRepository.save(post);                                    //zaten exception oluşmaz
    }

    public User saveUserToTable(User user) {
        if (userRepository.findByMail(user.getMail()) == null) {
            User createdUser = userRepository.save(user);
            return createdUser;
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
    public String returnUser(String username){
        User user=userRepository.findByUsername(username).get();
        return user.getUsername();
    }
    public User getUserDetails(String username){
        return userRepository.findByUsername(username).get();
    }

    public String extractUsername(String token){
        return jwtService.findUsername(token);
    }

}
