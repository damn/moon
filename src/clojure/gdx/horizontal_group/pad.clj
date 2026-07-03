(ns clojure.gdx.horizontal-group.pad
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn f [^HorizontalGroup group n]
  (HorizontalGroup/.pad group (float n)))
