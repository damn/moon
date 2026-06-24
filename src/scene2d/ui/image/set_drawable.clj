(ns scene2d.ui.image.set-drawable
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image)))

(defn f [^Image image drawable]
  (.setDrawable image drawable))
