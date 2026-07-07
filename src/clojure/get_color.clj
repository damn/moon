(ns clojure.get-color
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn f [batch]
  (Batch/.getColor batch))
