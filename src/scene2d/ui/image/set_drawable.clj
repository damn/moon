(ns scene2d.ui.image.set-drawable
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image :as image]))

(defn f [image drawable]
  (image/set-drawable! image drawable))
