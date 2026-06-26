(ns math.intersector.overlaps
  (:require [com.badlogic.gdx.math.intersector :as intersector]))

(defn overlaps? [circle rectangle]
  (intersector/overlaps? circle rectangle))
