(ns stage.windows
  (:require [scene2d.actor.set-name :refer [set-name!]]
            [scene2d.group :as group]
            [scene2d.group.add-actors :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (group/f)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
