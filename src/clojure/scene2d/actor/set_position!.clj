(ns clojure.scene2d.actor.set-position!
  (:require [clojure.scene2d.actor.set-position]))

(defn set-position!
  ([actor [x y]]
   (clojure.scene2d.actor.set-position/f actor x y))
  ([actor [x y] align]
   (clojure.scene2d.actor.set-position/f actor x y align)))
