(ns pjmusic.components.auto-complete-ajax
  (:require
    [ajax.core :as ajax]
    [clojure.string :as str]
    [goog.events.KeyCodes]
    [reagent.core :as r]))

(defn- wrap
  [index count]
  (mod (+ count index) count))

(defn- activate-suggestion-by-index
  [state index]
  (assoc state :active-index index))

(defn- activate-suggestion-next
  [{:as state :keys [active-index]} suggestions]
  (cond-> state
          (seq suggestions)
          (activate-suggestion-by-index (-> active-index
                                            (or -1)
                                            inc
                                            (wrap (count suggestions))))))

(defn- activate-suggestion-prev
  [{:as state :keys [active-index]} suggestions]
  (cond-> state
          (seq suggestions)
          (activate-suggestion-by-index (-> active-index
                                            (or 0)
                                            dec
                                            (wrap (count suggestions))))))

(defn- suggestion-includes-query?
  [query suggestion]
  (let [query (str/lower-case query)
        name (str/lower-case (:name suggestion))]
    (str/includes? name query)))

(defn- filter-suggestions
  [query suggestions]
  (if (str/blank? query)
    []
    (filter (partial suggestion-includes-query? query) suggestions)))

(defn- ajax-response
  [state-atom request-query-key]
  (fn [[ok result]]
    (when (= request-query-key (:query-key @state-atom))
      (if ok
        (swap! state-atom assoc
               :loading false
               :suggestions result)
        (swap! state-atom assoc
               :loading false
               :suggestions [{:id   0
                              :name "Server error!"}])))))

(defn- ajax-request
  [state-atom request]
  (let [{:keys [query-key]} @state-atom
        request (merge request
                       {:format          (ajax/url-request-format)
                        :response-format (ajax/json-response-format {:keywords? true})
                        :handler         (ajax-response state-atom query-key)})]
    (swap! state-atom assoc
           :loading true
           :suggestions [])
    (ajax/ajax-request request)))

(defn- on-change
  [state-atom event]
  (let [{:keys [query-key]} @state-atom
        new-query (-> event .-target .-value)
        new-query-key (first new-query)]
    (swap! state-atom assoc :query new-query :active-index nil)
    (when (and new-query-key (not= new-query-key query-key))
      (swap! state-atom assoc :query-key new-query-key)
      (ajax-request state-atom {:method :get
                                :uri    "/api/artists"
                                :params {:like new-query-key}}))))

(defn- on-key-down
  [state-atom display-suggestions event]
  (condp = (.-which event)
    goog.events.KeyCodes.UP
    (do
      (swap! state-atom activate-suggestion-prev display-suggestions)
      (.preventDefault event))

    goog.events.KeyCodes.DOWN
    (do
      (swap! state-atom activate-suggestion-next display-suggestions)
      (.preventDefault event))

    goog.events.KeyCodes.ENTER (js/console.log "enter")
    goog.events.KeyCodes.TAB (js/console.log "tab")
    true))

(defn auto-complete-ajax [id result-atom]
  (let [state-atom (r/atom {:query       ""
                            :query-key   nil
                            :loading     false
                            :suggestions []})]
    (fn []
      (let [{:keys [query suggestions active-index]} @state-atom
            display-suggestions (take 10 (filter-suggestions query suggestions))
            expanded? (seq display-suggestions)]
        [:div {:style {:display :inline-block}}
         [:input {:id          id
                  :type        :text
                  :value       (:query @state-atom)
                  :on-change   (partial on-change state-atom)
                  :on-key-down (partial on-key-down state-atom display-suggestions)}]
         [:div {:style {:position   :relative
                        :visibility (if expanded? :visible :hidden)}}
          [:div.suggestions {:style {:position :absolute
                                     :width    "100%"}}
           (for [[i {:keys [id name]}] (map vector (range) display-suggestions)
                 :let [selected? (= i active-index)]]
             ^{:key id}
             [:div.suggestion
              {:class (when selected? "active")}
              name])]]]))))