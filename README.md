### how to get this repo running locally:
1. Clone the repo
```
git clone https://github.com/m8bsd/blog.git
cd blog
```
2. Install dependencies
```
composer install
```
3. Set up the database
The app uses SQLite (configured in config.php to use database/blog.sqlite). Load the schema and seed it with fixtures using the Composer scripts:
```
composer run schema:load
composer run schema:fixtures
```

4. Start the development server
Point PHP's built-in server at the public/ directory (that's the webroot):
```
php -S localhost:8000 -t public
```
Then open http://localhost:8000 in your browser.

### A few notes:

1. - Make sure the database/ directory is writable so SQLite can create the .sqlite file.
2. - The config.php has debug => true by default, which is fine for local dev.
3. - No .env file or external database server needed — it's all SQLite out of the box.
