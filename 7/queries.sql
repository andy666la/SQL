---1----
select
count(*)
from
part_nyc
where  on_hand > 70;


---2----
select sum(
(select count(P.on_hand)
from part_nyc P, color C
where P.color = C.color_id AND color_name = 'Red'
)
+(
select count(S.on_hand)
from part_sfo S, color C
where S.color = C.color_id AND color_name = 'Red'
)
);
---3----
SELECT S.supplier_id, S.supplier_name
FROM supplier S
WHERE
(SELECT SUM(NYC.on_hand)
FROM part_nyc NYC
WHERE S.supplier_id = NYC.supplier)
>
(SELECT SUM(SFO.on_hand)
FROM part_sfo SFO
WHERE S.supplier_id = SFO.supplier)
ORDER BY S.supplier_id;

---4----
SELECT DISTINCT S.supplier_id, S.supplier_name
FROM supplier S, part_nyc NYC
WHERE S.supplier_id = NYC.supplier AND NYC.part_number IN
(
SELECT NYC1.part_number
FROM supplier S, part_nyc NYC1
WHERE S.supplier_id = NYC1.supplier
EXCEPT
SELECT SFO.part_number
FROM supplier S, part_sfo SFO
WHERE S.supplier_id = SFO.supplier
)
ORDER BY S.supplier_id;

---5----
UPDATE part_nyc
SET on_hand = on_hand - 10
WHERE on_hand >= 10;

---6----
DELETE FROM part_nyc
WHERE on_hand < 30;