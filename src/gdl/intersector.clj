(ns gdl.intersector
  (:require [com.badlogic.gdx.math.intersector :as intersector]))

(defn overlaps? [& args]
  (apply intersector/overlaps args))
