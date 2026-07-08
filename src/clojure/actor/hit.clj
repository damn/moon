(ns clojure.actor.hit
  (:refer-clojure :exclude [new remove])
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor x y touchable?]
  (Actor/.hit actor (float x) (float y) touchable?))
