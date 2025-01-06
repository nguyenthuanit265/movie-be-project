-- Enable bigserial extension
CREATE EXTENSION IF NOT EXISTS "bigserial-ossp";
-- Enable vector extension for AI features
CREATE EXTENSION IF NOT EXISTS vector;

-- Create enum types
CREATE TYPE user_role AS ENUM ('user', 'admin', 'moderator');

-- Users table
CREATE TABLE users (
                       id bigserial PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255),
                       full_name VARCHAR(255) NOT NULL,
                       role user_role NOT NULL DEFAULT 'user',
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       is_active BOOLEAN DEFAULT false,
                       social_provider VARCHAR(50),
                       social_id VARCHAR(255),
                       CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
    );

-- Movies table
CREATE TABLE movies (
                        id bigserial PRIMARY KEY ,
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
                        CONSTRAINT positive_vote_average CHECK (vote_average >= 0 AND vote_average <= 10),
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
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Reviews table
CREATE TABLE reviews (
                         id bigserial PRIMARY KEY ,
                         user_id bigserial NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         movie_id bigserial NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
                         content TEXT NOT NULL,
                         rating FLOAT,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT valid_rating CHECK (rating >= 0 AND rating <= 10),
                         CONSTRAINT one_review_per_user_movie UNIQUE (user_id, movie_id)
);

-- Movie-Genre relationship table
CREATE TABLE movie_genres (
                              movie_id bigserial REFERENCES movies(id) ON DELETE CASCADE,
                              genre_id bigserial REFERENCES genres(id) ON DELETE CASCADE,
                              PRIMARY KEY (movie_id, genre_id)
);

-- Movie-Cast relationship table
CREATE TABLE movie_casts (
                             movie_id bigserial REFERENCES movies(id) ON DELETE CASCADE,
                             cast_id bigserial REFERENCES casts(id) ON DELETE CASCADE,
                             character VARCHAR(255),
                             role VARCHAR(100),
                             PRIMARY KEY (movie_id, cast_id, character)
);

-- Watchlist table
CREATE TABLE watchlists (
                            user_id bigserial REFERENCES users(id) ON DELETE CASCADE,
                            movie_id bigserial REFERENCES movies(id) ON DELETE CASCADE,
                            added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (user_id, movie_id)
);

-- Favorites table
CREATE TABLE favorites (
                           user_id bigserial REFERENCES users(id) ON DELETE CASCADE,
                           movie_id bigserial REFERENCES movies(id) ON DELETE CASCADE,
                           added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (user_id, movie_id)
);

-- Search history table
CREATE TABLE search_history (
                                id bigserial PRIMARY KEY ,
                                user_id bigserial REFERENCES users(id) ON DELETE CASCADE,
                                query TEXT NOT NULL,
                                searched_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Movie vectors table for AI features
CREATE TABLE movie_vectors (
                               movie_id bigserial REFERENCES movies(id) ON DELETE CASCADE PRIMARY KEY,
                               embedding vector(384), -- Assuming using 384-dimensional embeddings
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);