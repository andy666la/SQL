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
select supplier_id, supplier_name
from supplier S
where count(
select on_hand
from part_nyc N
where N.supplier = S.supplier_id
)
>
count(
select on_hand
from part_sfo SFO
where SFO.supplier = S.supplier_id
);

---4----
select supplier_id, supplier_name
from supplier S, part_nyc N
where N.supplier = S.supplier_id AND N.part_number are not in (
select SFO.part_number
from supplier S, part_sfo SFO
where SFO.supplier = S.supplier_id
)ORDER BY S.supplier_id;

---5----
UPDATE part_nyc
SET on_hand = on_hand - 10
WHERE on_hand >= 10;

---6----
DELETE FROM part_nyc
WHERE on_hand < 30;