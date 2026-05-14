package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"
	"sync"
	"time"

	"github.com/gorilla/mux"
)

// BlogPost represents a blog post
type BlogPost struct {
	ID        int       `json:"id"`
	Title     string    `json:"title"`
	Content   string    `json:"content"`
	Author    string    `json:"author"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

// Blog store with mutex for thread safety
type BlogStore struct {
	posts  map[int]*BlogPost
	nextID int
	mu     sync.RWMutex
}

var store = &BlogStore{
	posts:  make(map[int]*BlogPost),
	nextID: 1,
}

// CreatePost creates a new blog post
func CreatePost(w http.ResponseWriter, r *http.Request) {
	var post BlogPost
	if err := json.NewDecoder(r.Body).Decode(&post); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	store.mu.Lock()
	post.ID = store.nextID
	post.CreatedAt = time.Now()
	post.UpdatedAt = time.Now()
	store.posts[post.ID] = &post
	store.nextID++
	store.mu.Unlock()

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(post)
}

// GetAllPosts returns all blog posts
func GetAllPosts(w http.ResponseWriter, r *http.Request) {
	store.mu.RLock()
	posts := make([]*BlogPost, 0, len(store.posts))
	for _, post := range store.posts {
		posts = append(posts, post)
	}
	store.mu.RUnlock()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(posts)
}

// GetPost returns a single blog post by ID
func GetPost(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "Invalid post ID", http.StatusBadRequest)
		return
	}

	store.mu.RLock()
	post, exists := store.posts[id]
	store.mu.RUnlock()

	if !exists {
		http.Error(w, "Post not found", http.StatusNotFound)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(post)
}

// UpdatePost updates an existing blog post
func UpdatePost(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "Invalid post ID", http.StatusBadRequest)
		return
	}

	var updatedPost BlogPost
	if err := json.NewDecoder(r.Body).Decode(&updatedPost); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	store.mu.Lock()
	post, exists := store.posts[id]
	if !exists {
		store.mu.Unlock()
		http.Error(w, "Post not found", http.StatusNotFound)
		return
	}

	post.Title = updatedPost.Title
	post.Content = updatedPost.Content
	post.Author = updatedPost.Author
	post.UpdatedAt = time.Now()
	store.mu.Unlock()

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(post)
}

// DeletePost deletes a blog post
func DeletePost(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	id, err := strconv.Atoi(vars["id"])
	if err != nil {
		http.Error(w, "Invalid post ID", http.StatusBadRequest)
		return
	}

	store.mu.Lock()
	_, exists := store.posts[id]
	if !exists {
		store.mu.Unlock()
		http.Error(w, "Post not found", http.StatusNotFound)
		return
	}

	delete(store.posts, id)
	store.mu.Unlock()

	w.WriteHeader(http.StatusNoContent)
}

func main() {
	r := mux.NewRouter()

	// Routes
	r.HandleFunc("/posts", CreatePost).Methods("POST")
	r.HandleFunc("/posts", GetAllPosts).Methods("GET")
	r.HandleFunc("/posts/{id}", GetPost).Methods("GET")
	r.HandleFunc("/posts/{id}", UpdatePost).Methods("PUT")
	r.HandleFunc("/posts/{id}", DeletePost).Methods("DELETE")

	// Welcome route
	r.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Welcome to the Blog API!\n\nAvailable endpoints:\n")
		fmt.Fprintf(w, "POST   /posts       - Create a new post\n")
		fmt.Fprintf(w, "GET    /posts       - Get all posts\n")
		fmt.Fprintf(w, "GET    /posts/{id}  - Get a specific post\n")
		fmt.Fprintf(w, "PUT    /posts/{id}  - Update a post\n")
		fmt.Fprintf(w, "DELETE /posts/{id}  - Delete a post\n")
	}).Methods("GET")

	port := "8080"
	fmt.Printf("Blog API server starting on port %s...\n", port)
	log.Fatal(http.ListenAndServe(":"+port, r))
}
