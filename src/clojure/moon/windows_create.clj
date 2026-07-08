(ns clojure.moon.windows-create
  (:require [clojure.scene2d.actor.set-name :as set-name]
            [clojure.scene2d.group :as group]))

(defn windows-create [ctx actor-fns]
  (let [group* (group/new)]
    (run! #(group/add-actor! group* %) (for [f actor-fns] (f ctx)))
    (doto group*
      (set-name/f "moon.ui.windows"))))
