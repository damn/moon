(ns com.badlogic.gdx.scenes.scene2d.ui.check-box
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.scenes.scene2d.ui CheckBox Skin)
           ))

(defn new [text skin]
  (CheckBox. ^String text ^Skin skin))

(defn set-checked! [^CheckBox checkbox checked?]
  (CheckBox/.setChecked checkbox checked?))

(defn checked? [^CheckBox checkbox]
  (CheckBox/.isChecked checkbox))
