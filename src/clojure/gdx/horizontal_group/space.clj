(ns clojure.gdx.horizontal-group.space
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn f [^HorizontalGroup group n]
  (HorizontalGroup/.space group (float n)))
