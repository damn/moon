(ns scene2d.actor.is-window-title-bar
  (:require [clojure.gdx.actor.get-parent :as get-parent]
            [com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [clojure.gdx.window.get-title-label :as get-title-label]
            [clojure.gdx.window.instance? :as window?]))

(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (get-parent/f actor)]
      (when-let [p (get-parent/f p)]
        (and (window?/f p)
             (= (get-title-label/f p) actor))))))
