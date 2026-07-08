(ns clojure.scene2d.actor.is-window-title-bar
  (:require
            [clojure.scene2d.actor.get-parent] [clojure.window :as window]
            [clojure.label :as label]))

(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (clojure.scene2d.actor.get-parent/f actor)]
      (when-let [p (clojure.scene2d.actor.get-parent/f p)]
        (and (instance? window/class p)
             (= (window/get-title-label p) actor))))))
