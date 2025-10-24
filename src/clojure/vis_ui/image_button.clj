(ns clojure.vis-ui.image-button
  (:import (com.badlogic.gdx.scenes.scene2d.utils Drawable)
           (com.kotcrab.vis.ui.widget VisImageButton)))

(defn create [^Drawable drawable]
  (VisImageButton. drawable))
