(ns stage.windows
  (:require [clojure.gdx.actor.set-name :as set-name]
            [clojure.gdx.group.add-actor :as add-actor]
            [clojure.gdx.group.new :as new-group]))

(defn create [ctx actor-fns]
  (let [group (new-group/f)]
    (run! #(add-actor/f group %) (for [f actor-fns] (f ctx)))
    (doto group
      (set-name/f "moon.ui.windows"))))
