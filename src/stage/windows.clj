(ns stage.windows
  (:require [clojure.gdx.scene2d.actor :refer [set-name!]]
            [clojure.gdx.scene2d.group.create :refer [create-group]]
            [clojure.gdx.scene2d.group.add-actor :refer [add-actors!]]))

(defn create [ctx actor-fns]
  (doto (create-group)
    (add-actors! (for [f actor-fns]
                   (f ctx)))
    (set-name! "moon.ui.windows")))
