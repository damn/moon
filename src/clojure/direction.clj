(ns clojure.direction
  (:require [clojure.normalise :as normalise]))

(defn f [[sx sy] [tx ty]]
  (normalise/f [(- (float tx) (float sx))
                (- (float ty) (float sy))]))
