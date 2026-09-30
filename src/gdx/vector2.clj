(ns gdx.vector2
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.math Vector2)))

(defn new [[x y]]
  (Vector2. (float x) (float y)))

(defn clojurize [v2]
  [(.x ^Vector2 v2) (.y ^Vector2 v2)])
