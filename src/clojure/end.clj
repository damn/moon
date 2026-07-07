(ns clojure.end
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn f [batch]
  (Batch/.end batch))
