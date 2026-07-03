(ns clojure.gdx.button-group.set-min-check-count
  (:import (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn f [^ButtonGroup button-group n]
  (ButtonGroup/.setMinCheckCount button-group (int n)))
