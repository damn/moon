(ns scene2d.actor.set-position
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn set-position!
  ([actor [x y]]
   (actor/set-position! actor x y))
  ([actor [x y] align]
   (actor/set-position! actor x y align)))
