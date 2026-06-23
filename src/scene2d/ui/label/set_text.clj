(ns scene2d.ui.label.set-text
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label)))

(defn f [^Label label text]
  (.setText label ^String text))
