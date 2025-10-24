(ns clojure.gdx.math.vector2
  (:import (com.badlogic.gdx.math Vector2)))

(defn ->clj [^Vector2 vector2]
  [(.x vector2)
   (.y vector2)])

(defn ^Vector2 ->java
  ([[x y]]
   (->java x y))
  ([x y]
   (Vector2. x y)))
