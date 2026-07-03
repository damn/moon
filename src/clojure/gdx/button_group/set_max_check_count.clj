(ns clojure.gdx.button-group.set-max-check-count
  (:import (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn f [^ButtonGroup button-group n]
  (ButtonGroup/.setMaxCheckCount button-group (int n)))
