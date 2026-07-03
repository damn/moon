(ns clojure.gdx.checkbox.is-checked?
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox)))

(defn f [^CheckBox checkbox]
  (CheckBox/.isChecked checkbox))
