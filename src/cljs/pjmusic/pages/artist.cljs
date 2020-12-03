(ns pjmusic.pages.artist
  (:require
    [ajax.core :as ajax]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]))

(rf/reg-event-db
  :set-artist
  (fn [db [_ artist]]
    (assoc db :artist artist)))

(rf/reg-event-fx
  :fetch-artist
  (fn [_ [_ id]]
    {:http-xhrio {:method          :get
                  :uri             (str "/api/artists/" id)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:set-artist]}}))

(rf/reg-event-fx
  :artist/init
  (fn [_ [_ id]]
    {:dispatch [:fetch-artist id]}))

(rf/reg-sub
  :artist
  (fn [db _]
    (:artist db)))

(defn artist-page []
  (when-let [{:keys [name releases appears-on]} @(rf/subscribe [:artist])]
    [:section
     [:h2 name]
     [:div.release-list
      (for [{:keys [id title released media-descr]} releases]
        [:div.release-item {:key id}
         [:a {:href (rfe/href :release {:id id})
              :title title}
          [:img {:src (str "/img/releases/" id)}]]
         [:p.title
          [:a {:href (rfe/href :release {:id id})} title]]
         [:p.released released " " media-descr]])]
     (when (seq appears-on)
       [:h2 "Appears on"])
     (when (seq appears-on)
       [:div.release-list
        (for [{:keys [id title artistid artistname released compilation media-descr]} appears-on]
          [:div.release-item {:key id}
           [:a {:href (rfe/href :release {:id id})
                :title title}
            [:img {:src (str "/img/releases/" id)}]]
           [:p.title
            [:a {:href (rfe/href :release {:id id})} title]]
           [:p.artist (if compilation "In: " "By: ")
            [:a {:href (rfe/href :artist {:id artistid})} artistname]]
           [:p.released released " " media-descr]])])]))