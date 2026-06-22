(ns gdl.actor.is-window-title-bar
  (:require [gdl.actor.get-parent :refer [get-parent]]
            [gdl.label :as label]
            [gdl.window :as window]
            [gdl.window.get-title-label :as get-title-label]))

; FIXME does not work
(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (get-parent actor)]
      (when-let [p (get-parent p)]
        (and (instance? window/class actor)
             (= (get-title-label/f p) actor))))))
