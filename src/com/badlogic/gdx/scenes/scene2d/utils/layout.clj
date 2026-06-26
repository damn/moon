(ns com.badlogic.gdx.scenes.scene2d.utils.layout
  (:import (com.badlogic.gdx.scenes.scene2d.utils Layout)))

(defn set-fill-parent! [^Layout layout bool]
  (.setFillParent layout bool))

(defn pack [^Layout layout]
  (.pack layout))
