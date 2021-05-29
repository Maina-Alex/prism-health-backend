package com.prismhealth.Controllers;

import com.prismhealth.Models.Category;
import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;
import com.prismhealth.Models.Users;
import com.prismhealth.services.AdminPostService;
import com.prismhealth.services.AdminStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin/posts")
@CrossOrigin
public class AdminPostControler {
    @Autowired
    private AdminPostService postService;

    @GetMapping()
    public List<Post> getAllPosts() {
        return postService.getPosts();

    }

    @PostMapping()
    public Post addPost(@RequestBody Post post) {
        return postService.savePost(post);
    }

    @GetMapping("/categories")
    public List<PostCategory> getAllPostCategories() {
        return postService.getPostCategories();

    }

    @PostMapping("/categories")
    public  PostCategory saveCategory(@RequestBody PostCategory category) {
        return postService.savePostCategory(category);

    }


    @CrossOrigin
    @PostMapping("/delete")
    public ResponseEntity deletePost(@RequestBody Post post) {
        return postService.deletePost(post);
    }

    @PostMapping("categories/delete")
    public ResponseEntity deleteCategory(@RequestBody  PostCategory category) {
        return postService.deletePostCategory(category);
    }

}
