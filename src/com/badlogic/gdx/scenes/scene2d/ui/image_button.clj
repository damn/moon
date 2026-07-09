(ns com.badlogic.gdx.scenes.scene2d.ui.image-button
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)
           ))

(defn new [^Drawable drawable]
  (ImageButton. drawable))
