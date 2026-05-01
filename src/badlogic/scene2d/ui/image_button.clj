(ns badlogic.scene2d.ui.image-button
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create [^Drawable drawable]
  (ImageButton. drawable))
