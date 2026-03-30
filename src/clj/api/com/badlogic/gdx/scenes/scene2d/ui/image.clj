(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.image
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn create [^Drawable drawable]
  (Image. drawable))
