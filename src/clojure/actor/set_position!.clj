(ns clojure.actor.set-position!
  (:require [clojure.actor.set-position]))

(defn set-position!
  ([actor [x y]]
   (clojure.actor.set-position/f actor x y))
  ([actor [x y] align]
   (clojure.actor.set-position/f actor x y align)))
