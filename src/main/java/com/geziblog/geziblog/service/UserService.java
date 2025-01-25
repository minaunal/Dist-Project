package com.geziblog.geziblog.service;
import com.geziblog.geziblog.controller.dto.findDTO;
import com.geziblog.geziblog.controller.repository.FollowRepository;
import com.geziblog.geziblog.controller.repository.PlaceRepository;
import com.geziblog.geziblog.controller.repository.PostRepository;
import com.geziblog.geziblog.controller.repository.UserRepository;
import com.geziblog.geziblog.entity.Following;
import com.geziblog.geziblog.entity.Place;
import com.geziblog.geziblog.entity.Post;
import com.geziblog.geziblog.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private JwtService jwtService;
    static List<String> names = new ArrayList<>();
    static String placenames;
    private static final String FIND_PLACE_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json";
    private static final String NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    @Value("${google.places.api-key}")
    private String googleApiKey;
    @Value("${rapid.key}")
    private String rapidApiKey;

    //KULLANICI İŞLEMLERİ
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
    public findDTO ifFollows(User follower, findDTO searching){
        List<Following> followings=followRepository.findByFollower_id(userRepository.findByUsername(follower.getUsername()).get().getId());
        boolean isfollowing=followings.stream().anyMatch(following -> following.getFollowing().getId()==getUser(searching.getKeyword()).getId());
        if(isfollowing==true){
            searching.setFollowed(true);
        }
        return searching;
    }

    //POST İŞLEMLERİ
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
    public void deletePost(Long post_id){
        postRepository.deleteById(post_id);
    }
    public Post savePostToUser(Post post, String username) {
        post.setUser(userRepository.findByUsername(username).get());
        return postRepository.save(post);
    }
    public List<Post> findPosts(String keyword){
        List<Post> allPosts = postRepository.findAll();

        List<Post> filteredPosts = allPosts.stream()
                .filter(post -> post.getBaslik().contains(keyword) || post.getMetin().contains(keyword) || post.getPlace().getPlaces().contains(keyword))
                .collect(Collectors.toList());

        return filteredPosts;
    }

    //MAPS İŞLEMLERİ

    public List<String> getTopRatedPlaces(String input, String arrivalDate, String departureDate) {
        try {
            names=new ArrayList<>();
            placenames="";
            RestTemplate restTemplate = new RestTemplate();
            List<String> combinedResults = new ArrayList<>();

            // İlk API çağrısı: findPlaceFromText
            String findPlaceUrl = FIND_PLACE_URL + "?fields=geometry&input=" + URLEncoder.encode(input, StandardCharsets.UTF_8)
                    + "&inputtype=textquery&key=" + googleApiKey;

            ResponseEntity<Map> findPlaceResponse = restTemplate.getForEntity(findPlaceUrl, Map.class);

            Map<String, Object> candidates = ((List<Map<String, Object>>) findPlaceResponse.getBody().get("candidates")).get(0);
            Map<String, Object> location = (Map<String, Object>) ((Map<String, Object>) candidates.get("geometry")).get("location");

            double lat = (double) location.get("lat");
            double lng = (double) location.get("lng");

            // İkinci API çağrısı: nearbySearch (Turistik Yerler)
            String touristAttractionsUrl = NEARBY_SEARCH_URL + "?keyword=tourist&location=" + lat + "," + lng
                    + "&radius=50000&type=tourist_attraction&key=" + googleApiKey;

            ResponseEntity<Map> touristResponse = restTemplate.getForEntity(touristAttractionsUrl, Map.class);
            List<Map<String, Object>> touristResults = (List<Map<String, Object>>) touristResponse.getBody().get("results");

            // Cafe API çağrısı
            String cafesUrl = NEARBY_SEARCH_URL + "?location=" + lat + "," + lng
                    + "&radius=50000&type=cafe&key=" + googleApiKey;

            ResponseEntity<Map> cafeResponse = restTemplate.getForEntity(cafesUrl, Map.class);
            List<Map<String, Object>> cafeResults = (List<Map<String, Object>>) cafeResponse.getBody().get("results");

            // Tourist Attraction
            touristResults.stream()
                    .sorted((place1, place2) -> Integer.compare(
                            (int) place2.getOrDefault("user_ratings_total", 0),
                            (int) place1.getOrDefault("user_ratings_total", 0)
                    ))
                    .limit(3)
                    .forEach(place -> names.add((String) place.get("name")));

            // Cafe
            cafeResults.stream()
                    .max(Comparator.comparingInt(cafe -> (int) cafe.getOrDefault("user_ratings_total", 0)))
                    .ifPresent(cafe -> names.add((String) cafe.get("name")));

            // Otel
            String hotelSearchUrl = "https://booking-com15.p.rapidapi.com/api/v1/hotels/searchHotelsByCoordinates" +
                    "?latitude=" + lat + "&longitude=" + lng + "&arrival_date=" + arrivalDate + "&departure_date=" + departureDate +
                    "&radius=10&units=metric&languagecode=en-us&currency_code=TRY";

            HttpHeaders headers = new HttpHeaders();
            headers.add("x-rapidapi-key", rapidApiKey);
            headers.add("x-rapidapi-host", "booking-com15.p.rapidapi.com");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> hotelResponse = restTemplate.exchange(hotelSearchUrl, HttpMethod.GET, entity, Map.class);
            List<Map<String, Object>> hotelResults = (List<Map<String, Object>>) ((Map<String, Object>) hotelResponse.getBody().get("data")).get("result");

            Map<String, Object> topHotel = hotelResults.stream()
                    .max(Comparator.comparingDouble(hotel -> {
                        Object reviewScore = hotel.get("review_score");
                        return reviewScore instanceof Number ? ((Number) reviewScore).doubleValue() : Double.MIN_VALUE;
                    }))
                    .orElse(hotelResults.isEmpty() ? null : hotelResults.get(0));

            if (topHotel != null) {
                String hotelName = (String) topHotel.get("hotel_name");

                String amountRounded = (String) ((Map<String, Object>) ((Map<String, Object>) topHotel.get("composite_price_breakdown")).get("net_amount")).get("amount_rounded");
                String hotelPrice = amountRounded.replace("TL", "").trim();  // Remove "TL" and any extra spaces

                names.add(hotelName + " (Fiyat: " + hotelPrice + " TRY)");

            }
            placenames = String.join(", ", names);
            // Sonuçlar
            return names;
        } catch (Exception e) {
            throw new RuntimeException("Yerler alınırken hata oluştu: " + e.getMessage());
        }
    }
    public String getPlaceNames(){
        return placenames;
    }
    public String getPlaceNamesFromPlace_id(Integer place_id){
        return placeRepository.findById(place_id).getPlaces();
    }
    public void savePlace(Place place){
        placeRepository.save(place);
    }
    public List <String> getSavedPlaces(String username){
        List <Place> rows=placeRepository.findAllByUser_id(userRepository.findByUsername(username).get().getId());
        List <String> places=new ArrayList<>();
        for (Place place : rows) {
            places.add(place.getPlaces());
        }
        return places;
    }

}
