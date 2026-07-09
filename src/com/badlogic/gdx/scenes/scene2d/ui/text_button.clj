(ns com.badlogic.gdx.scenes.scene2d.ui.text-button
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.scenes.scene2d.ui Skin TextButton)
           ))

(defn new [^String text ^Skin skin]
  (TextButton. text skin))
