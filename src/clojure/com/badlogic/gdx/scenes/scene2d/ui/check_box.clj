(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.check-box
  "A checkbox is a button that contains an image indicating the checked or unchecked state and a label."
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox Skin)))

(defn create [^String text ^Skin skin]
  (CheckBox. text skin))

(defn checked? [^CheckBox check-box]
  (.isChecked check-box))

(defn set-checked! [^CheckBox check-box checked?]
  (.setChecked check-box checked?))
