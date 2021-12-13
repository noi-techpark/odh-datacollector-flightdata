CREATE TABLE flightdata (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    timestamp TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    rawdata JSONB NOT NULL
);
CREATE INDEX flightdata_timestamp ON flightdata(timestamp);
