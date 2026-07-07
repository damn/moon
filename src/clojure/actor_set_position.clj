(ns clojure.actor-set-position
  (:require [clojure.set-position]))

(defn set-position!
  ([actor [x y]]
   (clojure.set-position/f actor x y))
  ([actor [x y] align]
   (clojure.set-position/f actor x y align)))
