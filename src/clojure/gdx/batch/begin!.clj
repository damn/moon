(ns clojure.gdx.batch.begin!
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn f [batch]
  (Batch/.begin batch))
