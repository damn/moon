(ns scene2d.ui.button-group.remove
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               ButtonGroup)))

(defn f! [button-group button]
  (ButtonGroup/.remove button-group ^Button button))
