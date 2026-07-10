(ns clojure.scene2d.actor.is-window-title-bar
  (:require
            [gdl.actor :as actor] [gdl.window :as window]
            [gdl.label :as label]))

(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (actor/get-parent actor)]
      (when-let [p (actor/get-parent p)]
        (and (instance? window/class p)
             (= (window/get-title-label p) actor))))))
