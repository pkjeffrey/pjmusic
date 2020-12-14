(ns pjmusic.model.artist
  (:require
    [clojure.string :as str]
    [pjmusic.db.core :as db]))

(defn by-id
  [id]
  (db/get-artist {:id id}))

(defn like
  [like]
  (let [like-lower (str "%" (str/lower-case like) "%")
        like-upper (str "%" (str/upper-case like) "%")]
    (db/get-artists-like {:like-lower like-lower
                          :like-upper like-upper})))