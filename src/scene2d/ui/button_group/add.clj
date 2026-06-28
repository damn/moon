(ns scene2d.ui.button-group.add
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup)))

(defn f! [^ButtonGroup button-group ^Button button]
  (ButtonGroup/.add button-group button))
