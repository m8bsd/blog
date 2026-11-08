package com.example.blog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleData {

    @Bean
    CommandLineRunner seed(PostRepository postRepo, CategoryRepository categoryRepo, CommentRepository commentRepo) {
        return args -> {
            if (categoryRepo.count() == 0) {
                Category general = new Category();
                general.setName("General");
                categoryRepo.save(general);

                Category tech = new Category();
                tech.setName("Tech");
                categoryRepo.save(tech);

                Post p = new Post();
                p.setTitle("Welcome to my blog");
                p.setAuthor("Admin");
                p.setContent("This is the first post.\n\nLog in as admin to write, edit and delete posts "
                        + "and to manage categories. Anyone can leave a comment below.");
                p.setCategory(general);
                postRepo.save(p);

                Comment c = new Comment();
                c.setPost(p);
                c.setName("Visitor");
                c.setBody("Nice to be here!");
                commentRepo.save(c);
            }
        };
    }
}
