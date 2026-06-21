(ns scene2d.actor.is-window
  (:require [clojure.scenes.scene2d.ui.window :as window]))

(defn f [actor]
  (instance? window/class actor))
