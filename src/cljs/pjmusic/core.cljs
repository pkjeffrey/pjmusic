(ns pjmusic.core
  (:require
    [clojure.string :as string]
    [day8.re-frame.http-fx]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [pjmusic.ajax :as ajax]
    [pjmusic.events]
    [pjmusic.pages.artist :refer [artist-page]]
    [pjmusic.pages.release :refer [release-page]]
    [pjmusic.pages.home :refer [home-page]]
    [re-frame.core :as rf]
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [reitit.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe])
  (:import goog.History))

(defn header []
  [:header
   [:div.left
    [:a {:href "#/"} [:img.logo {:src "/img/logo.png" :alt "Peter's Music logo"}]]
    [:a {:href "#/"} [:h1 "Peter's Music"]]]
   [:div.middle "Search"]
   [:div.right "User"]])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div.page
     [header]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/"
      {:name        :home
       :view        #'home-page
       :controllers [{:start (fn [_] (rf/dispatch [:home/init 12]))}]}]
     ["/artist/:id"
      {:name       :artist
       :view       #'artist-page
       :parameters {:path {:id pos-int?}}
       :controllers [{:parameters {:path [:id]}
                      :start (fn [params] (rf/dispatch [:artist/init (-> params :path :id)]))}]}]
     ["/release/:id"
      {:name       :release
       :view       #'release-page
       :parameters {:path {:id pos-int?}}
       :controllers [{:parameters {:path [:id]}
                      :start (fn [params] (rf/dispatch [:release/init (-> params :path :id)]))}]}]]
    {:coercion spec-coercion/coercion
     :compile  coercion/compile-request-coercers}))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
