(ns clojure.gdx.image.new-drawable
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn f [^Drawable drawable]
  (Image. drawable))
