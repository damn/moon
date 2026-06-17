(ns com.badlogic.gdx.scenes.scene2d.actor.hit
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn hit [^Actor actor [x y] touchable?]
  (.hit actor x y touchable?))
