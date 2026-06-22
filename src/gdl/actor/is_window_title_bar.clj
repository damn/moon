(ns gdl.actor.is-window-title-bar
  (:require [gdl.actor.get-parent :refer [get-parent]]
            [gdl.label :as label]
            [gdl.window.get-title-label :as get-title-label])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Window)))

; FIXME does not work
(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (get-parent actor)]
      (when-let [p (get-parent p)]
        (and (instance? Window actor)
             (= (get-title-label/f p) actor))))))
