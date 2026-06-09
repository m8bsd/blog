### Project Structure
```
blog/
├── main.go
├── go.mod
├── templates/
│   ├── layout.templ
│   ├── home.templ
│   └── post.templ
├── static/
│   └── css/
└── generated/
```
### Install Dependencies
```
go mod init blog

go get github.com/a-h/templ
go get github.com/go-chi/chi/v5

go install github.com/a-h/templ/cmd/templ@latest
```

