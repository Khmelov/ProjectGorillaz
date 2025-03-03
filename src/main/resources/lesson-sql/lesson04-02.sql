DROP TABLE IF EXISTS player;

CREATE TABLE player
(
    id          BIGSERIAL PRIMARY KEY,
    login       VARCHAR(128) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    role        VARCHAR(50),
    game_points INT,
    date        DATE
);

INSERT INTO player
    (login, password, role, game_points, date)
VALUES ('Carl', 'admin', 'ADMIN', 15, '2022-01-12'),
       ('Alisa', 'qwerty', 'USER', 7, '2023-03-03'),
       ('Bob', 'asdfg', 'GUEST', 3, '2024-03-05'),
       ('Mike', 'mikepass', 'USER', 2, '2025-01-01'),
       ('Elon', 'musk', 'USER', 16, '2024-11-11'),
       ('Joe', 'qwerty', 'GUEST', 7, '2024-11-11'),
       ('Olga', 'asdfg', 'ADMIN', 13, '2024-11-11'),
       ('Jack', 'zxcvb', 'USER', 4, '2024-11-11'),
       ('Michael', '12345', 'USER', 9, '2023-10-12');

SELECT id, login, role, game_points, date
  FROM player
 WHERE id > 0
   AND role = 'GUEST'
 ORDER BY id DESC;

SELECT *
  FROM player
 WHERE login ILIKE '%a%';

SELECT *
  FROM player
 WHERE date < NOW();

SELECT SUM(game_points) all_points, role
  FROM player
 GROUP BY role;

SELECT *
  FROM player
 ORDER BY game_points;
;

   UPDATE player
      SET game_points=(SELECT MIN(player.game_points) FROM player) + 1,
          role='ADMIN'
    WHERE login = 'Bob'
RETURNING id,login,role,game_points;

SELECT MIN(game_points)
  FROM player;


   DELETE
     FROM player
    WHERE game_points = (SELECT MIN(game_points) FROM player)
RETURNING id, login;

SELECT id
  FROM player
 WHERE role = 'ADMIN';

SELECT login
  FROM player p
 WHERE id IN (10, 16);

SELECT *
  FROM player;
SELECT AVG(game_points)
  FROM player;

SELECT CASE
           WHEN (15) > player.game_points
               THEN 'Winner'
           ELSE 'Loser'
           END AS title
  FROM player;

SELECT p.role,
       COUNT(game_points),
       SUM(game_points) AS s,
       AVG(game_points)
  FROM player p
 GROUP BY (role)
 ORDER BY s DESC;

SELECT *
  FROM (SELECT p.role,
               COUNT(game_points),
               SUM(game_points) AS s,
               AVG(game_points)
          FROM player p
         GROUP BY (role)
         ORDER BY s DESC)
 WHERE s > 10;

SELECT id, id, login, password, role, game_points, EXTRACT(DOY FROM date) weeks
  FROM player;


SELECT ROW_NUMBER() OVER (PARTITION BY role),
       id,
       login,
       role,
       player.game_points,
       AVG(game_points) OVER (PARTITION BY role),
       SUM(game_points) OVER (PARTITION BY role)
  FROM player;

SELECT DENSE_RANK() OVER (PARTITION BY role ORDER BY game_points),
       id,
       login,
       role
  FROM player