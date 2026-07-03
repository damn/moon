(ns gdx.math.rectangle.contains
  (:refer-clojure :exclude [contains?])
  (:require [clojure.gdx.rectangle.contains :as contains]))

(defn contains? [rectangle [x y]]
  (contains/f rectangle x y))
