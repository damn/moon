(ns clojure.scene2d.actor.set-position
  (:refer-clojure :exclude [new remove])
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f
  ([actor x y]
   (actor/set-position actor x y))
  ([actor x y align]
   (actor/set-position actor x y align)))
