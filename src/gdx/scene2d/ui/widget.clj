(ns gdx.scene2d.ui.widget
  (:require [com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]))

(defn f
  [{:keys [draw!]}]
  (widget/new draw!))
