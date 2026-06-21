(ns scene2d.actor.is-window
  (:require [clojure.ui.window :as window]))

(defn f [actor]
  (instance? window/class actor))
