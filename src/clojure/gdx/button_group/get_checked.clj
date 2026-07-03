(ns clojure.gdx.button-group.get-checked
  (:import (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn f [^ButtonGroup button-group]
  (ButtonGroup/.getChecked button-group))
