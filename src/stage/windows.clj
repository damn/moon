(ns stage.windows
  (:require [clojure.gdx.actor.set-name :as set-name])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn create [ctx actor-fns]
  (let [group (Group.)]
    (run! #(Group/.addActor group %) (for [f actor-fns] (f ctx)))
    (doto group
      (set-name/f "moon.ui.windows"))))
