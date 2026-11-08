package com.example.blog;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class CategoryController {

    private final CategoryRepository categories;
    private final PostRepository posts;

    public CategoryController(CategoryRepository categories, PostRepository posts) {
        this.categories = categories;
        this.posts = posts;
    }

    @InitBinder("category")
    void binder(WebDataBinder binder) {
        binder.setAllowedFields("name");
    }

    @GetMapping("/categories")
    public String list(Model model) {
        model.addAttribute("categories", categories.findAllByOrderByNameAsc());
        model.addAttribute("category", new Category());
        return "categories";
    }

    @PostMapping("/categories")
    public String create(@Valid @ModelAttribute("category") Category category,
                         BindingResult result,
                         Model model) {
        if (!result.hasFieldErrors("name")) {
            category.setName(category.getName().trim());
            if (categories.existsByNameIgnoreCase(category.getName())) {
                result.rejectValue("name", "duplicate", "That category already exists");
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("categories", categories.findAllByOrderByNameAsc());
            return "categories";
        }
        categories.save(category);
        return "redirect:/categories";
    }

    @PostMapping("/categories/{id}/delete")
    @Transactional
    public String delete(@PathVariable Long id) {
        if (!categories.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        posts.clearCategory(id);   // posts in this category become uncategorised
        categories.deleteById(id);
        return "redirect:/categories";
    }
}
