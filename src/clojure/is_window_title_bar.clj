(ns clojure.is-window-title-bar
  (:require [clojure.window :as window]
            [clojure.actor :as actor]
            [clojure.label :as label]))

(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (actor/get-parent actor)]
      (when-let [p (actor/get-parent p)]
        (and (instance? window/class p)
             (= (window/get-title-label p) actor))))))
