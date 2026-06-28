(ns scene2d.actor.is-window-title-bar
  (:require [scene2d.actor.get-parent :refer [get-parent]]
            [scene2d.ui.window.get-title-label :as get-title-label])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label Window)))

; FIXME does not work
(defn f [actor]
  (when (instance? Label actor)
    (when-let [p (get-parent actor)]
      (when-let [p (get-parent p)]
        (and (instance? Window actor)
             (= (get-title-label/f p) actor))))))
