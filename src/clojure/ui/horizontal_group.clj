(ns clojure.ui.horizontal-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn create
  [{:keys [space pad]}]
  (doto (HorizontalGroup.)
    (.space space)
    (.pad pad)))
