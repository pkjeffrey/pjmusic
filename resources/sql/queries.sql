-- :name get-artists :? :*
SELECT ID,
       NAME
FROM ARTIST

-- :name get-artist-releases :? :*
SELECT ID,
       TITLE,
       RELEASED,
       COMPILATION
FROM RELEASE
WHERE ARTIST = :id
ORDER BY RELEASED, ADDED

-- :name get-artist-appears-on :? :*
SELECT DISTINCT R.ID,
                A.ID ARTISTID,
                A.NAME ARTISTNAME,
                R.TITLE,
                R.RELEASED,
                R.COMPILATION,
                R.ADDED
FROM RELEASE R
INNER JOIN ARTIST A
    ON A.ID = R.ARTIST
INNER JOIN MEDIA M
    ON M.RELEASE = R.ID
INNER JOIN TRACK T
    ON T.MEDIA = M.ID
WHERE T.ARTIST = :id
AND T.ARTIST <> R.ARTIST
ORDER BY R.RELEASED, R.ADDED

-- :name get-release-art :? :1
SELECT ART
FROM RELEASE
WHERE ID = :id

-- :name get-release-media-descrs :? :*
SELECT COUNT(*) CNT,
       F.NAME
FROM MEDIA M
INNER JOIN FORMAT F
    ON F.ID = M.FORMAT
WHERE M.RELEASE = :id
GROUP BY F.NAME, F.SORT
ORDER BY F.SORT

-- :name get-release-medias :? :*
SELECT M.ID,
       M.TITLE,
       F.NAME
FROM MEDIA M
INNER JOIN FORMAT F
    ON F.ID = M.FORMAT
WHERE M.RELEASE = :id
ORDER BY M.ID

-- :name get-media-tracks :? :*
SELECT T.SIDE,
       T.NUMBER,
       T.TITLE,
       A.ID ARTISTID,
       A.NAME ARTISTNAME
FROM TRACK T
INNER JOIN ARTIST A
    ON A.ID = T.ARTIST
WHERE T.MEDIA = :id
ORDER BY T.SIDE, T.NUMBER

-- ==================================================================

-- :name get-artist :? :1
SELECT ID,
       NAME
FROM ARTIST
WHERE ID = :id

-- :name get-artists-like :? :*
SELECT ID,
       NAME
FROM ARTIST
WHERE NAME LIKE :like-lower
OR NAME LIKE :like-upper

-- :name get-release :? :1
SELECT R.ID,
       A.ID "ARTIST-ID",
       A.NAME "ARTIST-NAME",
       R.TITLE,
       R.RELEASED,
       R.LABEL,
       R.CATALOG,
       R.COMPILATION,
       R.ADDED
FROM RELEASE R
INNER JOIN ARTIST A
    ON A.ID = R.ARTIST
WHERE R.ID = :id

-- :name get-recent-releases :? :*
SELECT R.ID,
       A.ID "ARTIST-ID",
       A.NAME "ARTIST-NAME",
       R.TITLE,
       R.RELEASED,
       R.COMPILATION,
       R.ADDED
FROM RELEASE R
INNER JOIN ARTIST A
    ON A.ID = R.ARTIST
ORDER BY R.ADDED DESC
FETCH FIRST :num ROWS ONLY

-- :name get-releases-by-artist :? :*
SELECT R.ID,
       A.ID "ARTIST-ID",
       A.NAME "ARTIST-NAME",
       R.TITLE,
       R.RELEASED,
       R.COMPILATION,
       R.ADDED
FROM RELEASE R
INNER JOIN ARTIST A
    ON A.ID = R.ARTIST
WHERE A.ID = :artist-id
ORDER BY R.RELEASED, R.ADDED

-- :name get-releases-feature-artist :? :*
SELECT DISTINCT R.ID,
                A.ID "ARTIST-ID",
                A.NAME "ARTIST-NAME",
                R.TITLE,
                R.RELEASED,
                R.COMPILATION,
                R.ADDED
FROM RELEASE R
INNER JOIN ARTIST A
    ON A.ID = R.ARTIST
INNER JOIN MEDIA M
    ON M.RELEASE = R.ID
INNER JOIN TRACK T
    ON T.MEDIA = M.ID
WHERE T.ARTIST = :artist-id
  AND T.ARTIST <> R.ARTIST
ORDER BY R.RELEASED, R.ADDED

-- :name get-medias-by-release :? :*
SELECT M.ID,
       F.NAME FORMAT,
       M.TITLE,
       (SELECT COUNT(*) FROM TRACK WHERE MEDIA = M.ID) "NUM-TRACKS"
FROM MEDIA M
INNER JOIN FORMAT F
    ON F.ID = M.FORMAT
WHERE M.RELEASE = :release-id
ORDER BY M.ID

-- :name get-tracks-by-media :? :*
SELECT T.ID,
       T.SIDE,
       T.NUMBER,
       T.TITLE,
       A.ID "ARTIST-ID",
       A.NAME "ARTIST-NAME"
FROM TRACK T
INNER JOIN ARTIST A
    ON A.ID = T.ARTIST
WHERE T.MEDIA = :media-id
ORDER BY T.SIDE, T.NUMBER
