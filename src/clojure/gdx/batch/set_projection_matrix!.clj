(ns clojure.gdx.batch.set-projection-matrix!
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn f [batch matrix4]
  (Batch/.setProjectionMatrix batch matrix4))
