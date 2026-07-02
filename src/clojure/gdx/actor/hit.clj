(ns clojure.gdx.actor.hit
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f [^Actor actor x y touchable?]
  (Actor/.hit actor (float x) (float y) touchable?))
