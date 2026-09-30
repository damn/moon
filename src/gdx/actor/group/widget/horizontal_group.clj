(ns gdx.actor.group.widget.horizontal-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn create
  [{:keys [space pad]}]
  (doto (HorizontalGroup.)
    (.space (float space))
    (.pad (float pad))))
