(ns scene2d.actor.hit
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn hit [actor x y touchable?]
  (actor/hit actor x y touchable?))
