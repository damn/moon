(ns clojure.actor.is-window-title-bar
  (:require [clojure.actor.get-parent :refer [get-parent]]
            [clojure.ui.label :as label]
            [clojure.ui.window :as window]
            [clojure.window.get-title-label :as get-title-label]))

; FIXME does not work
(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (get-parent actor)]
      (when-let [p (get-parent p)]
        (and (instance? window/class actor)
             (= (get-title-label/f p) actor))))))
