(ns scene2d.ui.widget
  (:require [clojure.gdx.widget.new :as new-widget]))

(defn f
  [{:keys [draw!]}]
  (new-widget/f draw!))
