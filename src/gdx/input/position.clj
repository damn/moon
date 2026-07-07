(ns gdx.input.position
  (:require [clojure.input :as input]))

(defn f [input]
  [(input/get-x input)
   (input/get-y input)])
