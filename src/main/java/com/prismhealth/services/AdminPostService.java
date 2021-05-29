package com.prismhealth.services;


import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;
import com.prismhealth.repository.PostCategoryRepository;
import com.prismhealth.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminPostService {

    @Autowired
    PostCategoryRepository postCategoryRepository;

    @Autowired
    PostRepository postRepository;

    public void savePostCategory(PostCategory postCategory){
        postCategoryRepository.save(postCategory);
    }


    public List<PostCategory> getPostCategories(){
      return   postCategoryRepository.findAll();
    }

    public void deletePostCategory(PostCategory postCategory){
        postCategoryRepository.delete(postCategory);
    }

    public void savePost(Post post){
        postRepository.save(post);

    }


    public List<Post> getPosts(){
        return postRepository.findAll();
    }

    public void deletePost(Post post){
        postRepository.delete(post);
    }
}
