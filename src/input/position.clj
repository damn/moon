(ns input.position
  (:require [clojure.gdx.input.get-x :as get-x]
            [clojure.gdx.input.get-y :as get-y]))

(defn f [input]
  [(get-x/f input)
   (get-y/f input)])
