CREATE TABLE wallets (
                         id UUID PRIMARY KEY,
                         document VARCHAR(14) NOT NULL UNIQUE,
                         balance DECIMAL(19, 2) NOT NULL,
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL
);