select count(*) 
from part_nyc
where on_hand > 70;


UPDATE part_nyc
SET on_hand = on_hand - 10
WHERE on_hand >= 10;

DELETE FROM part_nyc
WHERE on_hand < 30;