-- Create planets table
CREATE TABLE planets (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    CONSTRAINT unique_planet_name UNIQUE (name)
);

-- Create index on name for faster lookups
CREATE INDEX idx_planets_name ON planets(name);
