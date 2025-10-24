(ns clojure.gdx.scene2d.ui.image
  (:import (com.badlogic.gdx.scenes.scene2d.ui Image)))

(defn set-drawable! [image drawable]
  (Image/.setDrawable image drawable))

(defn set-align! [image align]
  (Image/.setAlign image align))

(defn set-scaling! [image scaling]
  (Image/.setScaling image scaling))
