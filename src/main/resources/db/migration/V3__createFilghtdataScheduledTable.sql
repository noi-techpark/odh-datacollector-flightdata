CREATE TABLE flightdata_scheduled (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    company VARCHAR(255) NOT NULL,
    timestamp TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    rawdata JSONB NOT NULL
);