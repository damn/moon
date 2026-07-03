(ns clojure.gdx.batch.set-color!
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn f [^Batch batch r g b a]
  (Batch/.setColor batch (float r) (float g) (float b) (float a)))
