(ns input.position
  (:require [clojure.gdx :as gdx]))

(defn f [input]
  [(gdx/input-get-x input)
   (gdx/input-get-y input)])
