(ns clojure.input.position
  (:require [com.badlogic.gdx.input :as input]))

(defn f [input]
  [(input/getX input)
   (input/getY input)])
