package com.prismhealth.services;

import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;

import com.prismhealth.files.FileSystemStorageService;

import com.prismhealth.repository.PostCategoryRepository;
import com.prismhealth.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminPostService {

    @Autowired
    PostCategoryRepository postCategoryRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    FileSystemStorageService storageService;

    public PostCategory savePostCategory(PostCategory postCategory) {
        return postCategoryRepository.save(postCategory);
    }

    public List<PostCategory> getPostCategories() {
        return postCategoryRepository.findAll();
    }

    public ResponseEntity<?> deletePostCategory(PostCategory postCategory) {
        postCategoryRepository.delete(postCategory);
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
