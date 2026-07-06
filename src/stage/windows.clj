(ns stage.windows
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.group.add-actor :as add-actor]
            [clojure.gdx.group.new :as new-group]))

(defn create [ctx actor-fns]
  (let [group (new-group/f)]
    (run! #(add-actor/f group %) (for [f actor-fns] (f ctx)))
    (doto group
      (actor/set-name! "moon.ui.windows"))))
