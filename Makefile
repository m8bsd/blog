.PHONY: generate run

generate:
	templ generate

run: generate
	go run .
