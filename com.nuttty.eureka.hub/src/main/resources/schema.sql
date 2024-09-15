CREATE UNIQUE INDEX unique_name_idx ON p_hubs (name) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_address_idx ON p_hubs (address) WHERE is_delete = false;
