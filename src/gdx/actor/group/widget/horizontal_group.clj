(ns gdx.actor.group.widget.horizontal-group
  (:require [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]))

(defn create
  [{:keys [space pad]}]
  (doto (horizontal-group/new)
    (horizontal-group/space 2)
    (horizontal-group/pad 2)))
