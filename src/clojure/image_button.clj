(ns clojure.image-button
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.utils Drawable)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)))

(defn new [^Drawable drawable]
  (ImageButton. drawable))
