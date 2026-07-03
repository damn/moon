(ns clojure.gdx.image-button.new
  (:import (com.badlogic.gdx.scenes.scene2d.utils Drawable)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)))

(defn f [^Drawable drawable]
  (ImageButton. drawable))
