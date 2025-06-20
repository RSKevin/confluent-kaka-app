-- Table for Kafka Consumer Configuration
CREATE TABLE kafka_consumer_config (
    id SERIAL PRIMARY KEY,
    topic_name VARCHAR(255) UNIQUE NOT NULL,
    group_id VARCHAR(255) NOT NULL,
    table_name VARCHAR(255) NOT NULL, -- The table to insert data into (e.g., cders_crd_consumer_data)
    function_name VARCHAR(255) NOT NULL, -- The PostgreSQL function to call (e.g., proocess_cders_crd)
    enabled BOOLEAN DEFAULT TRUE
);

-- Table for Kafka Producer Configuration
CREATE TABLE kafka_producer_config (
    id SERIAL PRIMARY KEY,
    topic_name VARCHAR(255) UNIQUE NOT NULL,
    client_id VARCHAR(255),
    acks VARCHAR(10) DEFAULT 'all',
    retries INT DEFAULT 0,
    batch_size INT DEFAULT 16384,
    linger_ms INT DEFAULT 1,
    buffer_memory BIGINT DEFAULT 33554432,
    enabled BOOLEAN DEFAULT TRUE
);

-- Table for Kafka Common Properties (Brokers, SSL, etc.)
CREATE TABLE kafka_common_properties (
    id SERIAL PRIMARY KEY,
    property_key VARCHAR(255) UNIQUE NOT NULL,
    property_value TEXT NOT NULL
);

-- Consumer Data Table (as you specified)
CREATE TABLE cders_crd_consumer_data (
    msg_id SERIAL PRIMARY KEY,
    json_data JSONB NOT NULL,
    created_ts TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_ts TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Example PostgreSQL Function (as you specified)
CREATE OR REPLACE FUNCTION proocess_cders_crd(p_msg_id INT)
RETURNS VOID AS $$
BEGIN
    -- Your processing logic here using p_msg_id to retrieve data from cders_crd_consumer_data
    RAISE NOTICE 'Processing message ID: %', p_msg_id;
    -- Example: Update the updated_ts or perform some business logic
    UPDATE cders_crd_consumer_data SET updated_ts = CURRENT_TIMESTAMP WHERE msg_id = p_msg_id;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update updated_ts on row update (optional but good practice)
CREATE OR REPLACE FUNCTION update_updated_ts()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_ts = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_cders_crd_consumer_data_updated_ts
BEFORE UPDATE ON cders_crd_consumer_data
FOR EACH ROW
EXECUTE FUNCTION update_updated_ts();