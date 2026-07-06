(ns input.position
  (:require
            [com.badlogic.gdx.input :as input]))

(defn f [input]
  [(input/get-x input)
   (input/get-y input)])
