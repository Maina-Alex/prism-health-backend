package com.prismhealth.Controllers;

import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;

import com.prismhealth.dto.Request.DeletePostReq;
import com.prismhealth.dto.Request.GetPostRequest;
import com.prismhealth.dto.Request.PostCategoryReq;
import com.prismhealth.dto.Request.PostRequest;
import com.prismhealth.services.AdminPostService;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/posts")
@CrossOrigin
public class AdminPostControler {
    @Autowired
    private AdminPostService postService;

    @ApiOperation(value = "gets all posts")
    @GetMapping()
    public List<Post> getAllPosts() {
        return postService.getPosts();

    }

    @PostMapping()
    public ResponseEntity<?> addPost(@RequestBody PostRequest req) {
        return postService.savePost(req);
    }

    @ApiOperation("gets a list of post categories title")
    @GetMapping("/categories")
    public List<String> getAllPostCategories() {
        return postService.getPostCategories();

    }

    @GetMapping("/post")
    public ResponseEntity<?> getPostById(@RequestBody GetPostRequest req) {
        return postService.getPost(req);

    }

    @GetMapping("/categorypost/{categoryId}")
    public List<Post> getPostsByCategoryId(@PathVariable String categoryId) {
        return postService.getPostsByCategory(categoryId);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getPostCategoryById(@PathVariable String id) {
        return postService.getPostsCategory(id);
    }
    @PostMapping("/categories")
    public ResponseEntity<?> saveCategory(@RequestBody PostCategoryReq req) {
        return postService.savePostCategory(req);
    }
    @PostMapping("/delete/")
    public ResponseEntity<?> deletePost(@RequestBody DeletePostReq req) {
        return postService.deletePost(req);
    }

    @PostMapping("categories/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        return postService.deletePostCategory(id);
    }

}
