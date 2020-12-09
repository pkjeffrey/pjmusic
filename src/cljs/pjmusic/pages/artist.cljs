(ns pjmusic.pages.artist
  (:require
    [ajax.core :as ajax]
    [pjmusic.pages.components :as comp]
    [re-frame.core :as rf]))

(rf/reg-event-db
  :set-artist
  (fn [{:keys [artist] :as db} [_ new-artist]]
    (assoc db :artist (merge artist new-artist))))

(rf/reg-event-db
  :set-artist-releases
  (fn [db [_ releases]]
    (assoc-in db [:artist :releases] releases)))

(rf/reg-event-db
  :set-artist-appears-on
  (fn [db [_ appears-on]]
    (assoc-in db [:artist :appears-on] appears-on)))

(rf/reg-event-fx
  :fetch-artist
  (fn [{:keys [db]} [_ id]]
    {:db (assoc db :artist {})
     :fx [[:http-xhrio {:method          :get
                        :uri             (str "/api/artists/" id)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :on-success      [:set-artist]}]
          [:http-xhrio {:method          :get
                        :uri             "/api/releases"
                        :params          {:by-artist id}
                        :response-format (ajax/json-response-format {:keywords? true})
                        :on-success      [:set-artist-releases]}]
          [:http-xhrio {:method          :get
                        :uri             "/api/releases"
                        :params          {:feature-artist id}
                        :response-format (ajax/json-response-format {:keywords? true})
                        :on-success      [:set-artist-appears-on]}]]}))

(rf/reg-event-fx
  :artist/init
  (fn [_ [_ id]]
    {:dispatch [:fetch-artist id]}))

(rf/reg-sub
  :artist
  (fn [db _]
    (:artist db)))

(rf/reg-sub
  :artist-name
  :<- [:artist]
  (fn [artist _]
    (:name artist)))

(rf/reg-sub
  :artist-releases
  :<- [:artist]
  (fn [artist _]
    (map #(dissoc % :artist-id) (:releases artist))))

(rf/reg-sub
  :artist-appears-on
  :<- [:artist]
  (fn [artist _]
    (:appears-on artist)))

(defn artist-page []
  (let [name @(rf/subscribe [:artist-name])
        releases @(rf/subscribe [:artist-releases])
        appears-on @(rf/subscribe [:artist-appears-on])]
    [:section
     [:h2 name]
     [comp/release-list releases]
     (when (seq appears-on) [:h2 "Appears on"])
     [comp/release-list appears-on]]))