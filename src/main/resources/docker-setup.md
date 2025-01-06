# Build the custom image

docker build -t postgres-vector:15 -f Dockerfile-postgres-vector .

# Run the container

docker run -d \
--name movie_postgres \
-e POSTGRES_DB=db_movie \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=123456 \
-p 5432:5432 \
postgres-vector:15

# Wait a few seconds for PostgreSQL to start

sleep 5

# Create the vector extension

docker exec -it movie_postgres psql -U postgres -d db_movie -c "CREATE EXTENSION IF NOT EXISTS vector;"

# Create volume for persistent data

docker volume create movie_pgdata

# Create network (optional, but good for connecting with other services)

docker network create movie_network

# Run PostgreSQL container

[//]: # (docker run -d --name movie_postgres -e POSTGRES_DB=db_movie -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -v movie_pgdata:/var/lib/postgresql/data -p 5432:5432 postgres)

docker run -d --name movie_postgres -e POSTGRES_DB=db_movie -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -v movie_pgdata:/var/lib/postgresql/data -p 5432:5432 postgres-vector:15

# Verify container is running

docker ps -a | grep movie_postgres

# Check logs

docker logs movie_postgres

# Connect to PostgreSQL using psql (if needed)

docker exec -it movie_postgres psql -U postgres -d db_movie

# Stop container

# docker stop movie_postgres

# Remove container

# docker rm movie_postgres

# Remove volume (careful - this deletes all data!)

# docker volume rm movie_pgdata