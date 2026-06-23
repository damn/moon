(ns stage.windows
  (:require [gdl.set-name :refer [set-name!]]
            [gdl.group :as group]
            [group.add-actors :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (group/f)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
