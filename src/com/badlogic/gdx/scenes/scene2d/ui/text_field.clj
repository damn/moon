(ns com.badlogic.gdx.scenes.scene2d.ui.text-field
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.scenes.scene2d.ui Skin TextField)
           ))

(defn new [^String text ^Skin skin]
  (TextField. text skin))

(defn get-text [^TextField text-field]
  (TextField/.getText text-field))
