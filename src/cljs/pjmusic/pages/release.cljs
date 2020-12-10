(ns pjmusic.pages.release
  (:require
    [ajax.core :as ajax]
    [pjmusic.pages.components :as comp]
    [re-frame.core :as rf]))

(rf/reg-event-db
  :set-release
  (fn [{:keys [release] :as db} [_ new-release]]
    (assoc db :release (merge release new-release))))

(rf/reg-event-db
  :set-media-tracks
  (fn [db [_ media-id tracks]]
    (let [sides (-> (map :side tracks) distinct count)
          medias (get-in db [:release :medias])
          new-medias (reduce (fn [acc {:keys [id] :as media}]
                               (conj acc (if (= media-id id)
                                           (assoc media :sides sides
                                                        :tracks tracks)
                                           media)))
                             [] medias)]
      (assoc-in db [:release :medias] new-medias))))

(rf/reg-event-fx
  :set-release-medias
  (fn [{:keys [db]} [_ release-id medias]]
    {:db (assoc-in db [:release :medias] medias)
     :fx (mapv (fn [{:keys [id]}]
                 [:http-xhrio {:method          :get
                               :uri             (str "/api/releases/" release-id "/medias/" id "/tracks")
                               :response-format (ajax/json-response-format {:keywords? true})
                               :on-success      [:set-media-tracks id]}])
               medias)}))

(rf/reg-event-fx
  :fetch-release
  (fn [{:keys [db]} [_ id]]
    {:db (assoc db :release {})
     :fx [[:http-xhrio {:method          :get
                        :uri             (str "/api/releases/" id)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :on-success      [:set-release]}]
          [:http-xhrio {:method          :get
                        :uri             (str "/api/releases/" id "/medias")
                        :response-format (ajax/json-response-format {:keywords? true})
                        :on-success      [:set-release-medias id]}]]}))

(rf/reg-event-fx
  :release/init
  (fn [_ [_ id]]
    {:dispatch [:fetch-release id]}))

(rf/reg-sub
  :release
  (fn [db _]
    (:release db)))

(rf/reg-sub
  :release-details
  :<- [:release]
  (fn [release _]
    (select-keys release [:id :title :artist-id :artist-name :compilation
                          :media-descr :released :label :catalog])))

(rf/reg-sub
  :release-medias
  :<- [:release]
  (fn [release _]
    (:medias release)))

(defn release-page []
  (let [{:keys [id compilation] :as release-details} @(rf/subscribe [:release-details])
        medias @(rf/subscribe [:release-medias])]
    (when id
      [:section
       [comp/release release-details]
       [comp/release-medias medias compilation]])))