(ns clojure.gdx.image.set-drawable
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn f [^Image image ^Drawable drawable]
  (Image/.setDrawable image drawable))
