(ns scene2d.actor.is-window-title-bar
  (:require [clojure.gdx :as gdx]))

(defn f [actor]
  (when (gdx/label? actor)
    (when-let [p (gdx/get-parent actor)]
      (when-let [p (gdx/get-parent p)]
        (and (gdx/window? p)
             (= (gdx/window-get-title-label p) actor))))))
