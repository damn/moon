(ns gdl.actor.is-window
  (:require [gdl.window :as window]))

(defn f [actor]
  (instance? window/class actor))
