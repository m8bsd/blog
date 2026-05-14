# Simple Blog API

A simple RESTful blog API built with Go featuring CRUD operations for blog posts.

## Features

- Create blog posts
- Read all posts or individual posts
- Update existing posts
- Delete posts
- Thread-safe in-memory storage
- JSON API responses

## Installation

1. Make sure you have Go installed (1.21 or later)
2. Install dependencies:
```bash
go mod download
```

## Running the Application

```bash
go run main.go
```

The server will start on `http://localhost:8080`

## API Endpoints

### Create a Post
```bash
POST /posts
Content-Type: application/json

{
  "title": "My First Post",
  "content": "This is the content of my first blog post.",
  "author": "John Doe"
}
```

### Get All Posts
```bash
GET /posts
```

### Get a Specific Post
```bash
GET /posts/{id}
```

### Update a Post
```bash
PUT /posts/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  "content": "Updated content",
  "author": "John Doe"
}
```

### Delete a Post
```bash
DELETE /posts/{id}
```

## Example Usage with cURL

### Create a post:
```bash
curl -X POST http://localhost:8080/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"Hello World","content":"This is my first post","author":"Jane"}'
```

### Get all posts:
```bash
curl http://localhost:8080/posts
```

### Get a specific post:
```bash
curl http://localhost:8080/posts/1
```

### Update a post:
```bash
curl -X PUT http://localhost:8080/posts/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Post","content":"Updated content","author":"Jane"}'
```

### Delete a post:
```bash
curl -X DELETE http://localhost:8080/posts/1
```

## Data Structure

Each blog post contains:
- `id` - Auto-generated unique identifier
- `title` - Post title
- `content` - Post content
- `author` - Post author
- `created_at` - Timestamp when post was created
- `updated_at` - Timestamp when post was last updated

## Notes

- Data is stored in memory and will be lost when the server stops
- The application uses gorilla/mux for routing
- Thread-safe operations using mutex locks
