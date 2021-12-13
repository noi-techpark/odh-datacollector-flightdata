CREATE TABLE genericdata (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    datasource VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    timestamp TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    rawdata JSONB NOT NULL
);
CREATE INDEX genericdata_timestamp ON genericdata(timestamp);
