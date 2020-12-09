(ns pjmusic.pages.home
  (:require
    [ajax.core :as ajax]
    [pjmusic.pages.components :as comp]
    [re-frame.core :as rf]))

(rf/reg-event-db
  :set-recents
  (fn [db [_ recents]]
    (assoc db :recents recents)))

(rf/reg-event-fx
  :fetch-recents
  (fn [_ [_ num]]
    {:http-xhrio {:method          :get
                  :uri             "/api/releases"
                  :params          {:recent num}
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:set-recents]}}))

(rf/reg-event-fx
  :home/init
  (fn [_ [_ num]]
    {:dispatch [:fetch-recents num]}))

(rf/reg-sub
  :recents
  (fn [db _]
    (:recents db)))

(defn home-page []
  [:section
   [:h2 "Recently added"]
   (when-let [recents @(rf/subscribe [:recents])]
     [comp/release-list recents])])