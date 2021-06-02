package com.prismhealth.services;

import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;

import com.prismhealth.dto.Request.*;

import com.prismhealth.repository.PostCategoryRepository;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminPostService {

    private final PostCategoryRepository postCategoryRepository;

    public ResponseEntity<?> savePostCategory(PostCategoryReq req) {
        boolean present=postCategoryRepository.findByCategoryName(req.getCategoryName()).isPresent();
        if(!present){
           PostCategory postCategory= postCategoryRepository.save(new PostCategory(req.getCategoryName(), req.getDescription()));
           return ResponseEntity.ok(postCategory);
        }
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
    public List<String> getPostCategories() {
    List<PostCategory>posts= postCategoryRepository.findAll();
    List<String> postCategories=new ArrayList<>();
    posts.forEach(p->{
        postCategories.add(p.getCategoryName());
    });
    return postCategories;
    }

    public ResponseEntity<?> deletePostCategory(String id) {
        postCategoryRepository.deleteById(id);
        return ResponseEntity.ok("Post Category Successfully Deleted.");
    }

    public ResponseEntity<?> updatePostCategory(@NonNull UpdatePostCatReq req){
        try{
            PostCategory category=postCategoryRepository.findById(req.getCategoryId()).get();
            if(req.getName()!=null)category.setCategoryName(req.getName());
            if(req.getDescription()!=null) category.setDescription(req.getDescription());
            postCategoryRepository.save(category);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }
        return null;
    }

    public PostCategory findPostCategoryByName(String name){
        return postCategoryRepository.findByCategoryName(name).orElse(null);
    }

    public ResponseEntity<?> savePost(PostRequest req) {
        PostCategory postCategory =findPostCategoryByName(req.getCategoryName());
        if(postCategory !=null){
            int counter= postCategory.getPostCounter()+1;
            Post newPost=new Post(counter,req.getPostTitle(), postCategory.getId(), req.getPostContent(), req.getImageUrl());
            List<Post> posts= postCategory.getPosts();
            posts.add(newPost);
            postCategory.setPosts(posts);
            postCategory.setPostCounter(counter);
            postCategoryRepository.save(postCategory);
            return ResponseEntity.ok(posts);
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Cannot find category name");

    }

    public List<Post> getPosts() {
        List<PostCategory>postCategories=postCategoryRepository.findAll();
        List<Post> posts=new ArrayList<>();
        postCategories.forEach(postCategory -> posts.addAll(postCategory.getPosts()));
        return posts;
    }

    public ResponseEntity<?> deletePost(DeletePostReq req) {
        PostCategory category=postCategoryRepository.findById(req.getCategoryId()).orElse(null);
        if(category!=null){
            List<Post> posts=category.getPosts()
                    .stream()
                    .filter(p->p.getId()!=req.getPostId()).collect(Collectors.toList());
            category.setPosts(posts);
            postCategoryRepository.save(category);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> updatePost(UpdatePostReq req){
        PostCategory postCategory=postCategoryRepository.findById(req.getCategoryId()).orElse(null);
        if(postCategory!=null){
            Post post=postCategory.getPosts().stream()
                    .filter(p->p.getId()== req.getPostId())
                    .findFirst()
                    .orElse(null);
            if(post!=null){
                List<Post> posts=postCategory.getPosts().stream()
                        .filter(p->p.getId()!=post.getId()).collect(Collectors.toList());
                if(req.getPostTitle()!=null)post.setPostTitle(req.getPostTitle());
                if(req.getPostContent()!=null)post.setPostContent(req.getPostContent());
                if(req.getImageUrl()!=null)post.setImageUrl(req.getImageUrl());
                posts.add(post);
                postCategory.setPosts(posts);
                postCategoryRepository.save(postCategory);
                return ResponseEntity.ok(post);
            }
        }
        return  new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

    }

    public ResponseEntity<Post> getPost(GetPostRequest req) {
        PostCategory category=postCategoryRepository.findById(req.getCategoryId()).orElse(null);
        if(category!=null){
            Post post=category.getPosts().stream().filter(p->p.getId()== req.getPostId()).findAny().orElse(null);
            if(post!=null){
                return ResponseEntity.ok(post);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> getPostsCategory(String id) {
        try{
            PostCategory postCategory=postCategoryRepository.findById(id).get();
            return  ResponseEntity.ok(postCategory);
        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List<Post> getPostsByCategory(String categoryId) {
        try{
            return postCategoryRepository.findById(categoryId).get().getPosts();
        }catch (Exception ex){
            return new ArrayList<>();
        }
    }
}
