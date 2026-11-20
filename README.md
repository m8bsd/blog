# Go Blog

A very small blog: Go + [templ](https://templ.guide) + HTMX + Bootstrap.
All source lives in `.templ` files; `templ generate` turns each one into a
`*_templ.go` file. Posts are stored in `posts.json` (created on first run).

## Run

```sh
go install github.com/a-h/templ/cmd/templ@latest   # once
go mod tidy                                         # fetches the templ library
templ generate                                      # creates *_templ.go files
go run .
```

Open http://localhost:8080 (set `ADDR=:3000` to change the port).

## Layout

- `main.templ` – routes and handlers (plain Go)
- `store.templ` – JSON-file post storage (plain Go)
- `templates.templ` – layout, index, post card, post page

## Routes

| Method | Path          | What it does                                  |
|--------|---------------|-----------------------------------------------|
| GET    | `/`           | List posts + new-post form                    |
| POST   | `/posts`      | Create post, returns card (HTMX prepends it)  |
| GET    | `/posts/{id}` | Single post page                              |
| DELETE | `/posts/{id}` | Delete post (HTMX removes the card)           |
