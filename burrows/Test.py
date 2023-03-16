select n1,n2, n3,n4 from(
    SELECT o1.id as id1, SELECT o2.id as id2, o1.name as n1, o2.name as n2 FROM 
    (select @id1:=@id1+1 as id, name from occupations, (select @id1:=0) dm   where occupation = "Doctor" order by name) o1
    LEFT JOIN
    (select @id2:=@id2+1 as id, name from occupations, (select @id2:=0) dm where occupation = "Professor" order by name) o2 
    ON o1.id = o2.id 
    union
    SELECT o1.id as id1, SELECT o2.id as id2, o1.name as n1, o2.name as n2 FROM 
    (select @id3:=@id3+1 as id, name from occupations, (select @id3:=0) dm   where occupation = "Doctor" order by name) o1
    right JOIN
    (select @id4:=@id4+1 as id, name from occupations, (select @id4:=0) dm where occupation = "Professor" order by name) o2 
    ON o1.id = o2.id) oo1
left join(
    SELECT o1.id as id3, SELECT o2.id as id4, o1.name as n3, o2.name as n4 FROM 
    (select @id5:=@id5+1 as id, name from occupations, (select @id5:=0) dm   where occupation = "Singer" order by name) o1
    LEFT JOIN
    (select @id6:=@id6+1 as id, name from occupations, (select @id6:=0) dm where occupation = "Actor" order by name) o2 
    ON o1.id = o2.id 
    union
    SELECT o1.id as id3, SELECT o2.id as id4, o1.name as n3, o2.name as n4 FROM 
    (select @id7:=@id7+1 as id, name from occupations, (select @id7:=0) dm   where occupation = "Singer" order by name) o1
    right JOIN
    (select @id8:=@id8+1 as id, name from occupations, (select @id8:=0) dm where occupation = "Actor" order by name) o2 
    ON o1.id = o2.id ) oo2
on oo1.id1=oo2.id3 and oo1.id2=oo2.id3 and oo1.id1=oo2.id4 and oo1.id2=oo2.id4;