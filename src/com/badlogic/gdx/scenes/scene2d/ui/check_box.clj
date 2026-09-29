(ns com.badlogic.gdx.scenes.scene2d.ui.check-box
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox Skin)))

(defn new [text skin]
  (CheckBox. ^String text ^Skin skin))

(defn setChecked [^CheckBox checkbox checked?]
  (.setChecked checkbox checked?))

(defn isChecked [^CheckBox checkbox]
  (.isChecked checkbox))
