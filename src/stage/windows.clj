(ns stage.windows
  (:require [clojure.gdx.scene2d.actor :refer [set-name!]]
            [clojure.gdx.scene2d.group.create :refer [create-group]]))

(defn create [ctx actor-fns]
  (doto (create-group
         {:group/actors (for [f actor-fns]
                          (f ctx))})
    (set-name! "moon.ui.windows")))
