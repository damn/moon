(ns clojure.ui-widget
  (:require [gdl.widget :as widget]))

(defn f
  [{:keys [draw!]}]
  (widget/new draw!))
