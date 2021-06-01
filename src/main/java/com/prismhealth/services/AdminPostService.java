package com.prismhealth.services;

import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;

import com.prismhealth.dto.Request.PostCategoryReq;
import com.prismhealth.files.FileSystemStorageService;

import com.prismhealth.repository.PostCategoryRepository;
import com.prismhealth.repository.PostRepository;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminPostService {

    private final PostCategoryRepository postCategoryRepository;
    private final PostRepository postRepository;
    private final FileSystemStorageService storageService;


    public PostCategory savePostCategory(PostCategoryReq req) {
        return postCategoryRepository.save(new PostCategory(req.getCategoryName(), req.getDescription()));
    }

    public List<PostCategory> getPostCategories() {
        return postCategoryRepository.findAll();
    }

    public ResponseEntity<?> deletePostCategory(String id) {
        postCategoryRepository.deleteById(id);
        return ResponseEntity.ok("Post Category Successfully Deleted.");
    }

    public Post savePost(Post post) {

        return postRepository.save(post);
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public ResponseEntity<?> deletePost(Post post) {
        postRepository.delete(post);
        return ResponseEntity.ok("Post Successfully Deleted.");
    }

    public Post getPost(String id) {
        return postRepository.findById(id).get();
    }

    public PostCategory getPostCategory(String id) {
        return postCategoryRepository.findById(id).get();
    }

    public List<Post> getPostsByCategory(String id) {

        return postRepository.findByPostCategoryId(id).get();
    }
}
