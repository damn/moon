(ns gdx.scene2d.ui.widget
  (:require [clojure.widget :as widget]))

(defn f
  [{:keys [draw!]}]
  (widget/new draw!))
