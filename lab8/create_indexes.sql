CREATE INDEX part_nyc_number_index
ON part_nyc (part_number);

CREATE INDEX part_nyc_supplier_index
ON part_nyc (supplier);

CREATE INDEX part_nyc_color_index
ON part_nyc (color);

CREATE INDEX part_nyc_on_hand_index
ON part_nyc (on_hand);