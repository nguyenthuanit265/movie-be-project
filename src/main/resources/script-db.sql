-- Enable bigserial extension
-- CREATE EXTENSION IF NOT EXISTS "bigserial-ossp";
-- Enable vector extension for AI features
CREATE EXTENSION IF NOT EXISTS vector;
-- Create enum types
-- CREATE TYPE user_role AS ENUM ('user', 'admin', 'moderator');
-- Users table
CREATE TABLE users (
                       id bigserial PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255),
                       full_name VARCHAR(255) NOT NULL,
                       ROLE VARCHAR(255) NOT NULL DEFAULT 'user',
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       is_active BOOLEAN DEFAULT FALSE,
                       social_provider VARCHAR(50),
                       social_id VARCHAR(255),
                       CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
    );
-- Movies table
CREATE TABLE movies (
                        id bigserial PRIMARY KEY ,
                        tmdb_id bigserial UNIQUE ,
                        title VARCHAR(255) NOT NULL,
                        original_title VARCHAR(255),
                        overview TEXT,
                        release_date DATE,
                        runtime FLOAT,
                        poster_path VARCHAR(255),
                        backdrop_path VARCHAR(255),
                        popularity FLOAT DEFAULT 0,
                        vote_average FLOAT DEFAULT 0,
                        vote_count INTEGER DEFAULT 0,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT positive_vote_average CHECK (vote_average >= 0
                            AND vote_average <= 10),
                        CONSTRAINT positive_vote_count CHECK (vote_count >= 0)
);
-- Genres table
CREATE TABLE genres (
                        id bigserial PRIMARY KEY ,
                        name VARCHAR(100) UNIQUE NOT NULL
);
-- Cast table
CREATE TABLE casts (
                       id bigserial PRIMARY KEY ,
                       name VARCHAR(255) NOT NULL,
                       profile_path VARCHAR(255),
                       biography TEXT,
                       birth_date DATE,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- Reviews table
CREATE TABLE reviews (
                         id bigserial PRIMARY KEY ,
                         user_id bigserial NOT NULL REFERENCES users(id) ON
                             DELETE
                             CASCADE,
                         movie_id bigserial NOT NULL REFERENCES movies(id) ON
                             DELETE
                             CASCADE,
                         content TEXT NOT NULL,
                         rating FLOAT,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT valid_rating CHECK (rating >= 0
                             AND rating <= 10),
                         CONSTRAINT one_review_per_user_movie UNIQUE (user_id,
                                                                      movie_id)
);
-- Movie-Genre relationship table
CREATE TABLE movie_genres (
                              movie_id bigserial REFERENCES movies(id) ON
                                  DELETE
                                  CASCADE,
                              genre_id bigserial REFERENCES genres(id) ON
                                  DELETE
                                  CASCADE,
                              PRIMARY KEY (movie_id,
                                           genre_id)
);
-- Movie-Cast relationship table
CREATE TABLE movie_casts (
                             movie_id bigserial REFERENCES movies(id) ON
                                 DELETE
                                 CASCADE,
                             cast_id bigserial REFERENCES casts(id) ON
                                 DELETE
                                 CASCADE,
                             CHARACTER VARCHAR(255),
                             ROLE VARCHAR(100),
                             PRIMARY KEY (movie_id,
                                          cast_id,
                                          CHARACTER)
);
-- Watchlist table
CREATE TABLE watchlists (
                            user_id bigserial REFERENCES users(id) ON
                                DELETE
                                CASCADE,
                            movie_id bigserial REFERENCES movies(id) ON
                                DELETE
                                CASCADE,
                            added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (user_id,
                                         movie_id)
);
-- Favorites table
CREATE TABLE favorites (
                           user_id bigserial REFERENCES users(id) ON
                               DELETE
                               CASCADE,
                           movie_id bigserial REFERENCES movies(id) ON
                               DELETE
                               CASCADE,
                           added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (user_id,
                                        movie_id)
);
-- Search history table
CREATE TABLE search_history (
                                id bigserial PRIMARY KEY ,
                                user_id bigserial REFERENCES users(id) ON
                                    DELETE
                                    CASCADE,
                                query TEXT NOT NULL,
                                searched_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- Movie vectors table for AI features
CREATE TABLE movie_vectors (
                               movie_id bigserial REFERENCES movies(id) ON
                                   DELETE
                                   CASCADE PRIMARY KEY,
                               embedding vector(384),
    -- Assuming using 384-dimensional embeddings
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movie_categories (
                                  id BIGSERIAL PRIMARY KEY,
                                  movie_id BIGINT REFERENCES movies(id),
                                  category VARCHAR(20) NOT NULL,
                                  created_at TIMESTAMP WITH TIME ZONE,
                                  updated_at TIMESTAMP WITH TIME ZONE,
                                  CONSTRAINT uk_movie_category UNIQUE (movie_id,
                                                                       category)
);

ALTER TABLE movies
    ADD COLUMN poster_url VARCHAR(255),
ADD COLUMN backdrop_url VARCHAR(255);

CREATE TABLE movie_trailers (
                                id BIGSERIAL PRIMARY KEY,
                                movie_id BIGINT REFERENCES movies(id),
                                KEY VARCHAR(50) NOT NULL,
                                name VARCHAR(255),
                                site VARCHAR(50),
                                TYPE VARCHAR(50),
                                official BOOLEAN DEFAULT FALSE,
                                published_at TIMESTAMP WITH TIME ZONE,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_movie_trailers_movie ON
    movie_trailers(movie_id);

CREATE INDEX idx_movie_trailers_published ON
    movie_trailers(published_at DESC);

ALTER TABLE reviews ADD COLUMN likes INTEGER DEFAULT 0;
-- Create movie_ratings table
CREATE TABLE movie_ratings (
                               id BIGSERIAL PRIMARY KEY,
                               movie_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               value FLOAT NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_movie_ratings_movie FOREIGN KEY (movie_id)
                                   REFERENCES movies(id) ON
                                       DELETE
                                       CASCADE,
                               CONSTRAINT fk_movie_ratings_user FOREIGN KEY (user_id)
                                   REFERENCES users(id) ON
                                       DELETE
                                       CASCADE,
                               CONSTRAINT valid_rating_value CHECK (value >= 0
                                   AND value <= 10),
                               CONSTRAINT unique_user_movie_rating UNIQUE (user_id,
                                                                           movie_id)
);
-- Create indexes for better performance
CREATE INDEX idx_movie_ratings_movie ON
    movie_ratings(movie_id);

CREATE INDEX idx_movie_ratings_user ON
    movie_ratings(user_id);

CREATE INDEX idx_movie_ratings_value ON
    movie_ratings(value);

CREATE TABLE password_reset_tokens (
                                       id BIGSERIAL PRIMARY KEY,
                                       token VARCHAR(255) NOT NULL,
                                       user_id BIGINT NOT NULL REFERENCES users(id),
                                       expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
                                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT uk_password_reset_token UNIQUE (token)
);

CREATE INDEX idx_password_reset_tokens_user ON
    password_reset_tokens(user_id);

CREATE INDEX idx_password_reset_tokens_token ON
    password_reset_tokens(token);

ALTER TABLE casts ADD COLUMN tmdb_id BIGINT UNIQUE;

ALTER TABLE genres
    ADD COLUMN tmdb_id BIGINT UNIQUE;

ALTER TABLE users
    ADD COLUMN provider VARCHAR(50),
ADD COLUMN provider_id VARCHAR(255),
ADD COLUMN image_url VARCHAR(255);

ALTER TABLE reviews
    ADD COLUMN tmdb_id VARCHAR(255) UNIQUE;
-- Production Companies
CREATE TABLE production_companies (
                                      id BIGSERIAL PRIMARY KEY,
                                      tmdb_id BIGINT,
                                      name VARCHAR(255),
                                      logo_path VARCHAR(255),
                                      origin_country VARCHAR(10),
                                      movie_id BIGINT REFERENCES movies(id) ON
                                          DELETE
                                          CASCADE
);
-- Origin Countries
CREATE TABLE movie_origin_countries (
                                        movie_id BIGINT REFERENCES movies(id) ON
                                            DELETE
                                            CASCADE,
                                        country_code VARCHAR(10),
                                        PRIMARY KEY (movie_id,
                                                     country_code)
);
-- Production Countries
CREATE TABLE movie_production_countries (
                                            movie_id BIGINT REFERENCES movies(id) ON
                                                DELETE
                                                CASCADE,
                                            iso_3166_1 VARCHAR(10),
                                            name VARCHAR(255),
                                            PRIMARY KEY (movie_id,
                                                         iso_3166_1)
);
-- Spoken Languages
CREATE TABLE movie_spoken_languages (
                                        movie_id BIGINT REFERENCES movies(id) ON
                                            DELETE
                                            CASCADE,
                                        english_name VARCHAR(255),
                                        iso_639_1 VARCHAR(10),
                                        name VARCHAR(255),
                                        PRIMARY KEY (movie_id,
                                                     iso_639_1)
);
-- Add new columns to movies table
ALTER TABLE movies
    ADD COLUMN adult BOOLEAN,
ADD COLUMN belongs_to_collection TEXT,
ADD COLUMN budget BIGINT,
ADD COLUMN homepage VARCHAR(255),
ADD COLUMN imdb_id VARCHAR(20),
ADD COLUMN original_language VARCHAR(10),
ADD COLUMN revenue BIGINT,
ADD COLUMN status VARCHAR(50),
ADD COLUMN tagline TEXT;

ALTER TABLE movies
    ADD COLUMN collection_id BIGINT,
ADD COLUMN collection_name VARCHAR(255),
ADD COLUMN collection_poster_path VARCHAR(255),
ADD COLUMN collection_backdrop_path VARCHAR(255);

ALTER TABLE movie_trailers
    ADD COLUMN tmdb_id BIGINT UNIQUE;

ALTER TABLE casts
    ADD COLUMN place_of_birth VARCHAR(255),
ADD COLUMN known_for_department VARCHAR(100),
ADD COLUMN popularity FLOAT,
ADD COLUMN gender VARCHAR(50),
ADD COLUMN imdb_id VARCHAR(20);