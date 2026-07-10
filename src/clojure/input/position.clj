(ns clojure.input.position
  (:require [gdl.input :as input]))

(defn f [input]
  [(input/get-x input)
   (input/get-y input)])
