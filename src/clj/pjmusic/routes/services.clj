(ns pjmusic.routes.services
  (:require
    [clojure.java.io :as io]
    [pjmusic.middleware.exception :as exception]
    [pjmusic.middleware.formats :as formats]
    [pjmusic.model.artist :as artist]
    [pjmusic.model.release :as release]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.coercion :as coercion]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.util.http-response :refer :all]
    [spec-tools.data-spec :as ds]))

(defn- get-artist
  [{{{:keys [id]} :path} :parameters}]
  (if-let [a (artist/get id)]
    (ok a)
    (not-found)))

(defn- get-release
  [{{{:keys [id]} :path} :parameters}]
  (if-let [r (release/get id)]
    (ok r)
    (not-found)))

(defn- recent-releases
  [{{{:keys [recent]} :query} :parameters}]
  (-> (or recent 10)
      release/recent
      ok))

(defn service-routes []
  ["/api"
   {:coercion   spec-coercion/coercion
    :muuntaja   formats/instance
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ["/artists/:id"
    {:get {:parameters {:path {:id pos-int?}}
           :handler    get-artist}}]

   ["/releases"
    [""
     {:get  {:parameters {:query {(ds/opt :recent)          pos-int?
                                  (ds/opt :by-artist)       pos-int?
                                  (ds/opt :features-artist) pos-int?}}
             :handler    get-releases}

      :post {;; TODO: create new release
             :handler (constantly created)}}]

    ["/:release-id"
     {:parameters {:path {:release-id pos-int?}}}

     [""
      {:get    {:handler get-release}

       :delete {;; TODO delete release
                :handler (constantly no-content)}

       :put    {;; TODO update release
                :handler (constantly ok)}}]

     ["/medias"
      [""
       {:get  {:handler get-medias}

        :post {;; TODO add media
               :handler (constantly created)}}]

      ["/:media-id"
       {:parameters {:path {:media-id pos-int?}}}

       [""
        {:get    {:handler get-media}

         :delete {;; TODO delete media
                  :handler (constantly no-content)}

         :put    {;; TODO update media
                  :handler (constantly ok)}}]

       ["/tracks"
        [""
         {:get  {:handler get-tracks}

          :post {;; TODO add track
                 :handler (constantly created)}}]

        ["/:track-id"
         {:parameters {:path {:track-id pos-int?}}}

         [""
          {:get    {:handler get-track}

           :delete {;; TODO delete track
                    :handler (constantly no-content)}

           :put    {;; TODO update track
                    :handler (constantly ok)}}]]]]]]]

   ["/ping"
    {:get (constantly (ok {:message "pong"}))}]

   ["/math"

    ["/plus"
     {:get  {:parameters {:query {:x int?, :y int?}}
             :responses  {200 {:body {:total pos-int?}}}
             :handler    (fn [{{{:keys [x y]} :query} :parameters}]
                           {:status 200
                            :body   {:total (+ x y)}})}
      :post {:parameters {:body {:x int?, :y int?}}
             :responses  {200 {:body {:total pos-int?}}}
             :handler    (fn [{{{:keys [x y]} :body} :parameters}]
                           {:status 200
                            :body   {:total (+ x y)}})}}]]

   ["/files"

    ["/upload"
     {:post {:parameters {:multipart {:file multipart/temp-file-part}}
             :responses  {200 {:body {:name string?, :size int?}}}
             :handler    (fn [{{{:keys [file]} :multipart} :parameters}]
                           {:status 200
                            :body   {:name (:filename file)
                                     :size (:size file)}})}}]

    ["/download"
     {:get {:handler (fn [_]
                       {:status  200
                        :headers {"Content-Type" "image/png"}
                        :body    (-> "public/img/warning_clojure.png"
                                     (io/resource)
                                     (io/input-stream))})}}]]])
