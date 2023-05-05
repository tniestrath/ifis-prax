package com.analysetool.api;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.analysetool.modells.Post;
import com.analysetool.repositories.PostRepository;

@RestController
@CrossOrigin
//@RequestMapping("/api/posts")
public class PostController {


    PostRepository postRepository;
    @Autowired
    public PostController(
            PostRepository postRepository
    ){
       this.postRepository = postRepository;
    }

    @GetMapping("/posts/getall")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/posts/publishedPosts")
    public List<Post> getPublishedPosts(){return postRepository.findPublishedPosts();}


    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id) {
        Optional<Post> postData = postRepository.findById(id);

        if (postData.isPresent()) {
            return new ResponseEntity<>(postData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        try {
            Post newPost = postRepository.save(post);
            return new ResponseEntity<>(newPost, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable("id") Long id, @RequestBody Post post) {
        Optional<Post> postData = postRepository.findById(id);

        if (postData.isPresent()) {
            Post updatedPost = postData.get();
            updatedPost.setTitle(post.getTitle());
            updatedPost.setContent(post.getContent());
            return new ResponseEntity<>(postRepository.save(updatedPost), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable("id") Long id) {
        try {
            postRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<HttpStatus> deleteAllPosts() {
        try {
            postRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
/*
    @GetMapping("/getPostsByAuthorLine")
    public String PostsByAuthor(@RequestParam int id) throws JSONException, ParseException {

        JSONArray list = new JSONArray();
        List<Post> posts = postRepository.findByAuthor(id);
        DateFormat onlyDate = new SimpleDateFormat("dd/MM/yyyy");
        if(!posts.isEmpty()){
            for(Post i:posts) {

                JSONObject obj = new JSONObject();
                Date Tag = onlyDate.parse(i.getDate().toString());

                if ( (!list.isNull(list.length()-1))   &&  (list.getJSONObject(list.length()-1).get("date") == Tag ))
                    { list.getJSONObject(list.length()-1).put("id",list.getJSONObject(list.length()-1).get("id")+","+i.getTitle()) ;}

                else{
                obj.put("id", i.getTitle());
                    obj.put("date", Tag);
                    list.put(obj);}
            }
        }
        return list.toString();
    }*/
@GetMapping("/getPostsByAuthorLine")
public String PostsByAuthor(@RequestParam int id) throws JSONException, ParseException {

    JSONArray list = new JSONArray();
    List<Post> posts = postRepository.findByAuthor(id);
    DateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd");

    if (!posts.isEmpty()) {
        for (Post i : posts) {
            JSONObject obj = new JSONObject();
            Date date = onlyDate.parse(i.getDate().toString());
            String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(date);

            obj.put("id", i.getTitle());
            obj.put("date", formattedDate);

            if (list.length() > 0 && list.getJSONObject(list.length() - 1).getString("date").equals(formattedDate)) {
                String currentId = list.getJSONObject(list.length() - 1).getString("id");
                list.getJSONObject(list.length() - 1).put("id", currentId + "," + i.getTitle());
            } else {
                list.put(obj);
            }
        }
    }
    return list.toString();
}






}

