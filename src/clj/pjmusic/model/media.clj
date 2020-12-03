(ns pjmusic.model.media
  (:require
    [pjmusic.db.core :as db]))

(defn by-release
  [release-id]
  (db/get-medias-by-release {:release-id release-id}))