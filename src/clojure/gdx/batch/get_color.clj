(ns clojure.gdx.batch.get-color
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(def f [batch]
  (Batch/.getColor batch))
