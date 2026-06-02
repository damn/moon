(ns clojure.gdx.scene2d.actor.hit
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn actor-hit [^Actor actor [x y] touchable?]
  (.hit actor x y touchable?))
