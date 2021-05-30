package com.prismhealth.repository;

import com.prismhealth.Models.Category;
import com.prismhealth.Models.Post;
import com.prismhealth.Models.PostCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PostCategoryRepository extends MongoRepository<PostCategory, String> {

}
