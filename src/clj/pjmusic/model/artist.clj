(ns pjmusic.model.artist
  (:require
    [pjmusic.db.core :as db]
    [pjmusic.model.release :refer [compilation->boolean media-description]]))

(defn- assoc-media-descr
  [{:keys [id] :as release}]
  (assoc release :media-descr (media-description id)))

(defn get
  [id]
  (let [artist (db/get-artist {:id id})
        releases (->> {:id id}
                      db/get-artist-releases
                      (map compilation->boolean)
                      (map assoc-media-descr))
        appears-on (->> {:id id}
                        db/get-artist-appears-on
                        (map compilation->boolean)
                        (map assoc-media-descr)
                        (map #(dissoc % :added)))]
    (assoc artist :releases releases
                  :appears-on appears-on)))