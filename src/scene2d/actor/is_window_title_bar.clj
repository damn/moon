(ns scene2d.actor.is-window-title-bar
  (:require [clojure.scenes.scene2d.actor.get-parent :refer [get-parent]]
            [clojure.scenes.scene2d.ui.label :as label]
            [clojure.scenes.scene2d.ui.window :as window]))

; FIXME does not work
(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (get-parent actor)]
      (when-let [p (get-parent p)]
        (and (instance? window/class actor)
             (= (window/title-label p) actor))))))
