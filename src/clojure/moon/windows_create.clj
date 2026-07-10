(ns clojure.moon.windows-create
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.scene2d.group :as group]))

(defn windows-create [ctx actor-fns]
  (let [group* (group/new)]
    (run! #(group/add-actor! group* %) (for [f actor-fns] (f ctx)))
    (doto group*
      (actor/setName "moon.ui.windows"))))
