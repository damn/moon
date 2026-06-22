(ns gdl.actor.is-window
  (:require [gdl.ui.window :as window]))

(defn f [actor]
  (instance? window/class actor))
