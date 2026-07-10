(ns com.badlogic.gdx.scenes.scene2d.utils.layout
  (:import (com.badlogic.gdx.scenes.scene2d.utils Layout)))

(defn pack [^Layout layout]
  (.pack layout))

(defn setFillParent [^Layout layout fill-parent?]
  (.setFillParent layout fill-parent?))
