package com.prismhealth.Controllers;

import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;

import com.prismhealth.services.AdminPostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable String id) {
        return postService.getPost(id);

    }

    @GetMapping("/categorypost/{categoryId}")
    public List<Post> getPostsByCategoryId(@PathVariable String categoryId) {
        return postService.getPostsByCategory(categoryId);

    }

    @GetMapping("/categories/{id}")
    public PostCategory getPostCategoryById(@PathVariable String id) {
        return postService.getPostCategory(id);

    }

    @PostMapping("/categories")
    public PostCategory saveCategory(@RequestBody PostCategory category) {
        return postService.savePostCategory(category);

    }

    @CrossOrigin
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody Post post) {
        return postService.deletePost(post);
    }

    @PostMapping("categories/delete")
    public ResponseEntity<?> deleteCategory(@RequestBody PostCategory category) {
        return postService.deletePostCategory(category);
    }

}
