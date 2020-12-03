(ns pjmusic.model.track
  (:require
    [pjmusic.db.core :as db]))

(defn by-media
  [media-id]
  (db/get-tracks-by-media {:media-id media-id}))