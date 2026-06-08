(ns clojure.math.vector2.direction
  (:require [clojure.math.vector2.normalise :as normalise]))

(defn f [[sx sy] [tx ty]]
  (normalise/f [(- (float tx) (float sx))
                (- (float ty) (float sy))]))
