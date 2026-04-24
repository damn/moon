(ns clojure.gdx.math.vector2
  (:import (com.badlogic.gdx.math Vector2)))

(defn ->java ^Vector2 [[x y]]
  (Vector2. x y))

(defn ->clj [^Vector2 v2]
  [(.x v2)
   (.y v2)])
