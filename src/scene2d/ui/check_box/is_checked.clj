(ns scene2d.ui.check-box.is-checked
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox)))

(defn f [^CheckBox check-box]
  (.isChecked check-box))
