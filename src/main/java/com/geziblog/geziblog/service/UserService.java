package com.geziblog.geziblog.service;
import com.geziblog.geziblog.controller.dto.findDTO;
import com.geziblog.geziblog.controller.repository.FollowRepository;
import com.geziblog.geziblog.controller.repository.PostRepository;
import com.geziblog.geziblog.controller.repository.UserRepository;
import com.geziblog.geziblog.entity.Following;
import com.geziblog.geziblog.entity.Post;
import com.geziblog.geziblog.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

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

    public User findUserbyId(int id){
        return userRepository.findById(id);
    }
    public User getUser(String username){
        return userRepository.findByUsername(username).get();
    }

    public void followUser(String follower1, String following1) {
        User follower=userRepository.findByUsername(follower1).get();
        User following=userRepository.findByUsername(following1).get();
        Following follow = new Following(); //Boş entity nesnesi
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
    }


    public Post savePostToUser(Post post, String username) {
        post.setUser(userRepository.findByUsername(username).get());
        return postRepository.save(post);
    }

    public List<Post> findPosts(String baslik, String metin){
        List<Post> allPosts = postRepository.findAll();

        List<Post> filteredPosts = allPosts.stream()
                .filter(post -> post.getBaslik().contains(baslik) || post.getMetin().contains(metin))
                .collect(Collectors.toList());

        return filteredPosts;
    }
    public findDTO ifFollows(User follower, findDTO searching){
       List<Following> followings=followRepository.findByFollower_id(userRepository.findByUsername(follower.getUsername()).get().getId());
       boolean isfollowing=followings.stream().anyMatch(following -> following.getFollowing().getId()==getUser(searching.getUsername()).getId());
       if(isfollowing==true){
            searching.setFollowed(true);
        }
        return searching;
    }

    @Transactional
    public List<Post> getPosts(int id){
        List<Post> posts= postRepository.findAllByUser_id(id);
        return posts;
    }

    @Transactional
    public List<Post> followingsPosts(int id){
        List<Post>posts=new ArrayList<>();
       List<Following> followings= followRepository.findByFollower_id(id);
        Set<Integer> uniqueIds = new HashSet<>();

        for (Following following : followings) {
            uniqueIds.add(following.getFollowing().getId());
        }
        List<Integer> uniqueIdsList = new ArrayList<>(uniqueIds);

        for (Integer uniqueId : uniqueIdsList) {
            List<Post>newPosts=postRepository.findAllByUser_id(uniqueId.intValue());
            posts.addAll(newPosts);
        }
        return posts;
    }

}
