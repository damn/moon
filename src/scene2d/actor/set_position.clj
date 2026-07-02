(ns scene2d.actor.set-position
  (:require [clojure.gdx.actor.set-position :as set-position]))

(defn set-position!
  ([actor [x y]]
   (set-position/f actor x y))
  ([actor [x y] align]
   (set-position/f actor x y align)))
