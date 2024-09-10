CREATE UNIQUE INDEX unique_address_idx ON p_companies (address) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_product_name_idx ON p_products (product_name) WHERE is_delete = false;
