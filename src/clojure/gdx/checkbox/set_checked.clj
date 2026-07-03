(ns clojure.gdx.checkbox.set-checked
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox)))

(defn f [^CheckBox checkbox checked?]
  (CheckBox/.setChecked checkbox checked?))
