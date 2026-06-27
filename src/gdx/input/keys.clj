(ns gdx.input.keys
  (:require [com.badlogic.gdx.input$keys :as keys]))

(defn k->value [k]
  (case k
    :input.keys/d keys/d
    :input.keys/a keys/a
    :input.keys/w keys/w
    :input.keys/s keys/s
    :input.keys/minus keys/minus
    :input.keys/equals keys/equals
    :input.keys/p keys/p
    :input.keys/space keys/space
    :input.keys/escape keys/escape
    :input.keys/i keys/i
    :input.keys/e keys/e
    :input.keys/enter keys/enter
    :input.keys/left keys/left
    :input.keys/right keys/right
    :input.keys/up keys/up
    :input.keys/down keys/down))
