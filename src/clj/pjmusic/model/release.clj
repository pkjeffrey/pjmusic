(ns pjmusic.model.release
  (:require
    [clojure.string :as str]
    [pjmusic.db.core :as db]))

(defn media-description
  [id]
  (->> {:id id}
       db/get-release-media-descrs
       (map (fn [{:keys [cnt name]}]
              (if (= 1 cnt) name (str cnt name))))
       (str/join "+")))

(defn compilation->boolean
  [{:keys [compilation] :as release}]
  (assoc release :compilation (= "Y" compilation)))

(defn get
  [id]
  (let [release (->> {:id id}
                     db/get-release
                     compilation->boolean)
        media-descr (media-description id)
        medias (->> {:id id}
                    db/get-release-medias
                    (map (fn [{:keys [id] :as media}]
                           (assoc media :tracks (db/get-media-tracks {:id id})))))]
    (assoc release :media-descr media-descr
                   :medias medias)))

(defn image
  [id]
  (db/get-release-image id))

(defn recent
  [number]
  (->> {:num number}
       db/get-recent-releases
       (map (fn [{:keys [id compilation] :as release}]
              (assoc release :compilation (= "Y" compilation)
                             :media-descr (media-description id))))))