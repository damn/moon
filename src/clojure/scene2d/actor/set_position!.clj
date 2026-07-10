(ns clojure.scene2d.actor.set-position!
  (:require [gdl.actor :as actor]))

(defn set-position!
  ([actor [x y]]
   (actor/set-position actor x y))
  ([actor [x y] align]
   (actor/set-position actor x y align)))
