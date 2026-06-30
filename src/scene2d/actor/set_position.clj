(ns scene2d.actor.set-position
  (:require [clojure.gdx :as gdx]))

(defn set-position!
  ([actor [x y]]
   (gdx/set-position! actor x y))
  ([actor [x y] align]
   (gdx/set-position! actor x y align)))
