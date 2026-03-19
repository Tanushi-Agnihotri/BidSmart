CREATE TABLE seller_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    store_name VARCHAR(100) NOT NULL,
    business_category VARCHAR(50) NOT NULL,
    description TEXT,
    legal_name VARCHAR(100) NOT NULL,
    id_document_url VARCHAR(255) NOT NULL,
    account_holder_name VARCHAR(100) NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    routing_number VARCHAR(50) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_seller_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
