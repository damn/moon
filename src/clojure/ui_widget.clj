(ns clojure.ui-widget
  (:require [gdl.scenes.scene2d.ui.widget :as widget]))

(defn f
  [{:keys [draw!]}]
  (widget/new draw!))
