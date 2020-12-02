(ns pjmusic.app
  (:require [pjmusic.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
