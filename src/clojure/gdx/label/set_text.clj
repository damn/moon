(ns clojure.gdx.label.set-text
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label)))

(defn f [^Label label ^String text]
  (Label/.setText label text))
