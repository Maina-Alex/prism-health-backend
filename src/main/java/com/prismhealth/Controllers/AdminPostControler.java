package com.prismhealth.Controllers;

import com.prismhealth.Models.Category;
import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;
import com.prismhealth.Models.Users;
import com.prismhealth.services.AdminPostService;
import com.prismhealth.services.AdminStaffService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Post> getAllPosts(@PathVariable("phone") String phone) {
        return staffService.getStaffById(phone);

    }

    @PostMapping()
    public String addPost(@RequestBody Users users, Principal principal) {
        return staffService.addUser(users, principal);
    }

    @GetMapping("/categories")
    public List<PostCategory> getAllPostCategories() {
        return staffService.getAllStaff();

    }

    @PostMapping("/categories")
    public  void saveCategory(@RequestBody Category category) {
        return staffService.getAllStaff();

    }


    @CrossOrigin
    @PostMapping("/delete")
    public boolean deletePost(@PathVariable  String postId) {
        return staffService.deleteStaff(users.getPhone());
    }

    @PostMapping("categories/delete")
    public boolean deleteCategory(@RequestBody  PostCategory category) {
        return postService.deletePostCategory(category);
    }

}
