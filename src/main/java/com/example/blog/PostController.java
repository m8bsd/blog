package com.example.blog;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class PostController {

    private final PostRepository posts;
    private final CategoryRepository categories;
    private final CommentRepository comments;

    public PostController(PostRepository posts, CategoryRepository categories, CommentRepository comments) {
        this.posts = posts;
        this.categories = categories;
        this.comments = comments;
    }

    // Only these fields can be set from a submitted form.
    @InitBinder("post")
    void postBinder(WebDataBinder binder) {
        binder.setAllowedFields("title", "author", "content");
    }

    @InitBinder("comment")
    void commentBinder(WebDataBinder binder) {
        binder.setAllowedFields("name", "body");
    }

    // ---------- public ----------

    @GetMapping("/")
    public String index(@RequestParam(required = false) Long category, Model model) {
        model.addAttribute("posts", category == null
                ? posts.findAllByOrderByCreatedAtDesc()
                : posts.findByCategoryIdOrderByCreatedAtDesc(category));
        model.addAttribute("categories", categories.findAllByOrderByNameAsc());
        model.addAttribute("selectedCategory", category);
        return "index";
    }

    @GetMapping("/posts/{id}")
    public String show(@PathVariable Long id, Model model) {
        Post post = findPost(id);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments.findByPostIdOrderByCreatedAtAsc(id));
        model.addAttribute("comment", new Comment());
        return "post";
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @Valid @ModelAttribute("comment") Comment comment,
                             BindingResult result,
                             Model model) {
        Post post = findPost(id);
        if (result.hasErrors()) {
            model.addAttribute("post", post);
            model.addAttribute("comments", comments.findByPostIdOrderByCreatedAtAsc(id));
            return "post";
        }
        comment.setPost(post);
        comments.save(comment);
        return "redirect:/posts/" + id + "#comments";
    }

    // ---------- admin ----------

    @GetMapping("/posts/new")
    public String newForm(Model model) {
        model.addAttribute("post", new Post());
        addFormData(model, null);
        return "form";
    }

    @PostMapping("/posts")
    public String create(@Valid @ModelAttribute("post") Post post,
                         BindingResult result,
                         @RequestParam(required = false) Long categoryId,
                         Model model) {
        if (result.hasErrors()) {
            addFormData(model, categoryId);
            return "form";
        }
        post.setCategory(findCategory(categoryId));
        Post saved = posts.save(post);
        return "redirect:/posts/" + saved.getId();
    }

    @GetMapping("/posts/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Post post = findPost(id);
        model.addAttribute("post", post);
        addFormData(model, post.getCategory() == null ? null : post.getCategory().getId());
        return "form";
    }

    @PostMapping("/posts/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("post") Post form,
                         BindingResult result,
                         @RequestParam(required = false) Long categoryId,
                         Model model) {
        if (result.hasErrors()) {
            form.setId(id);
            addFormData(model, categoryId);
            return "form";
        }
        Post post = findPost(id);
        post.setTitle(form.getTitle());
        post.setAuthor(form.getAuthor());
        post.setContent(form.getContent());
        post.setCategory(findCategory(categoryId));
        posts.save(post);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id) {
        posts.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        Comment comment = comments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        Long postId = comment.getPost().getId();
        comments.delete(comment);
        return "redirect:/posts/" + postId + "#comments";
    }

    // ---------- helpers ----------

    private Post findPost(Long id) {
        return posts.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    private Category findCategory(Long id) {
        return id == null ? null : categories.findById(id).orElse(null);
    }

    private void addFormData(Model model, Long categoryId) {
        model.addAttribute("categories", categories.findAllByOrderByNameAsc());
        model.addAttribute("categoryId", categoryId);
    }
}
