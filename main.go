package main

import (
    "log"
    "net/http"
    "strconv"

    "github.com/a-h/templ"
    "github.com/go-chi/chi/v5"

    "blog/templates"
)

var posts = []templates.Post{
    {ID: 1, Title: "First Post"},
    {ID: 2, Title: "Second Post"},
}

var content = map[int]string{
    1: "Welcome to the first post.",
    2: "This is the second post.",
}

func main() {
    r := chi.NewRouter()

    r.Get("/", homeHandler)
    r.Get("/post/{id}", postHandler)

    log.Println("Listening on :8080")
    http.ListenAndServe(":8080", r)
}

func homeHandler(w http.ResponseWriter, r *http.Request) {
    templates.Home(posts).Render(r.Context(), w)
}

func postHandler(w http.ResponseWriter, r *http.Request) {
    id, _ := strconv.Atoi(chi.URLParam(r, "id"))

    title := ""
    body := content[id]

    for _, p := range posts {
        if p.ID == id {
            title = p.Title
            break
        }
    }

    templ.Handler(
        templates.PostView(title, body),
    ).ServeHTTP(w, r)
}
