(ns pjmusic.pages.home
  (:require
    [ajax.core :as ajax]
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
   [:div.release-list
    (when-let [recents @(rf/subscribe [:recents])]
      (for [{:keys [id title artistid artistname released compilation media-descr]} recents]
        [:div.release-item {:key id}
         [:a {:href  (str "#/release/" id)
              :title title}
          [:img {:src (str "/img/releases/" id)}]]
         [:p.title
          [:a {:href (str "#/release/" id)} title]]
         [:p.artist (if compilation "In: " "By: ")
          [:a {:href (str "#/artist/" artistid)} artistname]]
         [:p.released released " " media-descr]]))]])