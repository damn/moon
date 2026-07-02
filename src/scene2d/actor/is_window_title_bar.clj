(ns scene2d.actor.is-window-title-bar
  (:require [clojure.gdx.actor.get-parent :as get-parent])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Label
                                               Window)))

(defn f [actor]
  (when (instance? Label actor)
    (when-let [p (get-parent/f actor)]
      (when-let [p (get-parent/f p)]
        (and (instance? Window actor)
             (= (Window/.getTitleLabel p) actor))))))
