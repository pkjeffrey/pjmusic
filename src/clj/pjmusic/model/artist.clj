(ns pjmusic.model.artist
  (:require
    [pjmusic.db.core :as db]))

(defn by-id
  [id]
  (db/get-artist {:id id}))