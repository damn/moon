(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.check-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox Skin)))

(defn create [^String text ^Skin skin]
  (CheckBox. text skin))

(defn checked? [^CheckBox check-box]
  (.isChecked check-box))

(defn set-checked! [^CheckBox check-box checked?]
  (.setChecked check-box checked?))
