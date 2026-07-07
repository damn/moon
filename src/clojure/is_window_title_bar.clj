(ns clojure.is-window-title-bar
  (:require
            [clojure.get-parent] [clojure.window :as window]
            [clojure.label :as label]))

(defn f [actor]
  (when (instance? label/class actor)
    (when-let [p (clojure.get-parent/f actor)]
      (when-let [p (clojure.get-parent/f p)]
        (and (instance? window/class p)
             (= (window/get-title-label p) actor))))))
