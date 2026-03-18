CREATE TABLE watchlist (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    auction_id UUID NOT NULL,
    added_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_watchlist_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_watchlist_auction FOREIGN KEY (auction_id) REFERENCES auctions(id),
    CONSTRAINT uk_watchlist_user_auction UNIQUE (user_id, auction_id)
);

CREATE INDEX idx_watchlist_user_id ON watchlist(user_id);
CREATE INDEX idx_watchlist_auction_id ON watchlist(auction_id);
