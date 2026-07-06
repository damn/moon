(ns com.badlogic.gdx.scenes.scene2d.ui.checkbox
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox Skin)))

(defn new [text skin]
  (CheckBox. ^String text ^Skin skin))

(defn set-checked [^CheckBox checkbox checked?]
  (CheckBox/.setChecked checkbox checked?))

(defn is-checked? [^CheckBox checkbox]
  (CheckBox/.isChecked checkbox))
