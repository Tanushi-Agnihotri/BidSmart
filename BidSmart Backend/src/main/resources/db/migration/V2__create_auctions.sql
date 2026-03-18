CREATE TABLE auctions (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    condition VARCHAR(30) NOT NULL,
    base_price NUMERIC(12, 2) NOT NULL,
    current_bid NUMERIC(12, 2) NOT NULL DEFAULT 0,
    bid_increment NUMERIC(10, 2) NOT NULL,
    total_bids INTEGER NOT NULL DEFAULT 0,
    watchlist_count INTEGER NOT NULL DEFAULT 0,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL,
    seller_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_auctions_seller FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT chk_auctions_status CHECK (status IN ('ACTIVE', 'UPCOMING', 'ENDING_SOON', 'CLOSED')),
    CONSTRAINT chk_auctions_condition CHECK (condition IN ('NEW', 'LIKE_NEW', 'EXCELLENT', 'VERY_GOOD', 'GOOD', 'FAIR', 'RESTORED')),
    CONSTRAINT chk_auctions_base_price CHECK (base_price > 0),
    CONSTRAINT chk_auctions_bid_increment CHECK (bid_increment > 0),
    CONSTRAINT chk_auctions_end_after_start CHECK (end_time > start_time)
);

CREATE INDEX idx_auctions_seller_id ON auctions(seller_id);
CREATE INDEX idx_auctions_status ON auctions(status);
CREATE INDEX idx_auctions_category ON auctions(category);
CREATE INDEX idx_auctions_end_time ON auctions(end_time);
