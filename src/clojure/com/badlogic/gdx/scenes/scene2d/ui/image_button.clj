(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.image-button
  "A button with a child [[clojure.com.badlogic.gdx.scenes.scene2d.ui.image]] to display an image. This is useful when the button must be larger than the image and the
  image centered on the button. If the image is the size of the button, a [[clojure.com.badlogic.gdx.scenes.scene2d.ui.button]] without any children can be used, where
  the up, down, and checked nine patches define
  the image."
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create [^Drawable drawable]
  (ImageButton. drawable))
