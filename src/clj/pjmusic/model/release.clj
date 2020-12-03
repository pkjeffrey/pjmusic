(ns pjmusic.model.release
  (:require
    [clojure.string :as str]
    [pjmusic.db.core :as db]))

(defn- media-description
  [id]
  (->> {:id id}
       db/get-release-media-descrs
       (map (fn [{:keys [cnt name]}]
              (if (= 1 cnt) name (str cnt name))))
       (str/join "+")))

(defn- db->release
  [{:keys [id compilation] :as db-release}]
  (assoc db-release :compilation (= "Y" compilation)
                    :media-descr (media-description id)))

(defn- select-releases
  [db-fn params]
  (->> (db-fn params)
       (map db->release)))

(defn by-id
  [id]
  (-> {:id id}
      db/get-release
      db->release))

(defn image
  [id]
  (db/get-release-image id))

(defn recent
  [number]
  (select-releases db/get-recent-releases {:num number}))

(defn by-artist
  [artist-id]
  (select-releases db/get-releases-by-artist {:artist-id artist-id}))

(defn feature-artist
  [artist-id]
  (select-releases db/get-releases-feature-artist {:artist-id artist-id}))