DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'CHALLENGE_DATABASE') THEN
      CREATE DATABASE CHALLENGE_DATABASE;
   END IF;
END
$$;
