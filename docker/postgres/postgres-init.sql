-- Simple table design for development purpose to store flight data

CREATE TABLE flightdata (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    body JSONB NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL
);
CREATE INDEX flightdata_created_at ON flightdata(created_at);

CREATE TABLE genericdata (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    datasource VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    timestamp TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    rawdata JSONB NOT NULL
);
CREATE INDEX genericdata_timestamp ON genericdata(timestamp);