(ns gdl.image-button
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create [^Drawable image-up]
  (ImageButton. image-up))
