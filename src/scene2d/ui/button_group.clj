(ns scene2d.ui.button-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn create [{:keys [max-check-count
                      min-check-count]}]
  (doto (ButtonGroup.)
    (.setMaxCheckCount max-check-count)
    (.setMinCheckCount min-check-count)))
