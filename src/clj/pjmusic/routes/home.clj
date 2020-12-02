(ns pjmusic.routes.home
  (:require
    [clojure.java.io :as io]
    [pjmusic.db.core :as db]
    [pjmusic.layout :as layout]
    [pjmusic.middleware :as middleware]
    [pjmusic.model.release :as releases]
    [ring.util.http-response :as response]
    [ring.util.response]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok "something I wrote")
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/img/releases/:id" {:get (fn [{{:keys [id]} :path-params}]
                                (-> (or (releases/image id)
                                        (io/input-stream (io/resource "public/img/placeholder.jpg")))
                                    response/ok
                                    (response/header "Content-Type" "image/jpg")))}]])