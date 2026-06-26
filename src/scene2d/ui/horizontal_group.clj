(ns scene2d.ui.horizontal-group
  (:require [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]))

(defn create
  [{:keys [space pad]}]
  (doto (horizontal-group/create)
    (horizontal-group/set-space! space)
    (horizontal-group/set-pad! pad)))
