# Check that Java is installed:
```
java -version
```
```
javac -version
```
# run a single source file
```
javac main.java
```
```
java main
```

# Simple Blog (Spring Boot)

A small blog with posts, categories, comments and an admin login.

**Stack:** Spring Boot 3.3, Thymeleaf, Spring Data JPA, Spring Security, H2 (in-memory), Bean Validation. Java 17+.

## Run

```bash
mvn spring-boot:run
```

Then open http://localhost:8080

Data lives in memory and resets on restart. Sample categories, a post and a comment are added on startup (see `SampleData.java`).

## Admin login

Default credentials: `admin` / `changeme`.

Change them before sharing the site, either in `application.properties` or with environment variables:

```bash
BLOG_ADMIN_USERNAME=me BLOG_ADMIN_PASSWORD='a-long-password' mvn spring-boot:run
```

## Who can do what

| Action                                   | Visitors | Admin |
|------------------------------------------|:--------:|:-----:|
| Read posts, filter by category           | yes      | yes   |
| Leave a comment                          | yes      | yes   |
| Create, edit, delete posts               |          | yes   |
| Add or delete categories                 |          | yes   |
| Delete comments                          |          | yes   |

Deleting a category keeps its posts; they become uncategorised.
Deleting a post also deletes its comments.

## Pages

| URL                  | What it does                         |
|----------------------|--------------------------------------|
| `/`                  | List posts (`/?category=ID` filters) |
| `/posts/{id}`        | Read a post and its comments         |
| `/login`             | Admin login                          |
| `/posts/new`         | Write a post (admin)                 |
| `/posts/{id}/edit`   | Edit a post (admin)                  |
| `/categories`        | Manage categories (admin)            |

## Keep data between restarts

In `application.properties`, switch the datasource URL to a file database:

```
spring.datasource.url=jdbc:h2:file:./data/blogdb
```

The sample data is only added when the database has no categories, so it won't be duplicated.

## Notes

- Comments are open to everyone with no spam protection. If you get spam, add a CAPTCHA or require approval before comments show.
- There is a single admin account stored in memory. For several users, replace the `UserDetailsService` bean in `SecurityConfig.java` with a database-backed one.
- Serve it over HTTPS if it is exposed to the internet, since the login form sends the password.
