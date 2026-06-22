(ns stage.windows
  (:require [gdl.set-name :refer [set-name!]]
            [gdl.group.create :refer [create-group]]
            [gdl.group.add-actors :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (create-group)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
