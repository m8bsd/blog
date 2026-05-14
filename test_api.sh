#!/bin/bash

# Test script for Blog API
# Make sure the server is running before executing this script

BASE_URL="http://localhost:8080"

echo "========================================="
echo "Blog API Test Script"
echo "========================================="
echo ""

# Test 1: Create a post
echo "1. Creating a new blog post..."
curl -X POST $BASE_URL/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Getting Started with Go",
    "content": "Go is an amazing language for building web services.",
    "author": "Alice"
  }'
echo -e "\n"

# Test 2: Create another post
echo "2. Creating another blog post..."
curl -X POST $BASE_URL/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Building REST APIs",
    "content": "REST APIs are the backbone of modern web applications.",
    "author": "Bob"
  }'
echo -e "\n"

# Test 3: Get all posts
echo "3. Getting all blog posts..."
curl $BASE_URL/posts
echo -e "\n"

# Test 4: Get a specific post
echo "4. Getting post with ID 1..."
curl $BASE_URL/posts/1
echo -e "\n"

# Test 5: Update a post
echo "5. Updating post with ID 1..."
curl -X PUT $BASE_URL/posts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Getting Started with Go (Updated)",
    "content": "Go is an amazing language for building fast and reliable web services.",
    "author": "Alice"
  }'
echo -e "\n"

# Test 6: Get the updated post
echo "6. Getting updated post..."
curl $BASE_URL/posts/1
echo -e "\n"

# Test 7: Delete a post
echo "7. Deleting post with ID 2..."
curl -X DELETE $BASE_URL/posts/2
echo -e "\n"

# Test 8: Verify deletion
echo "8. Getting all posts (should only show 1 post)..."
curl $BASE_URL/posts
echo -e "\n"

echo "========================================="
echo "Test completed!"
echo "========================================="
