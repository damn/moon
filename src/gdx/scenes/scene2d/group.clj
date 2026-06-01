(ns gdx.scenes.scene2d.group
  (:require [clojure.gdx.scene2d.group :as group]
            [clojure.gdx.scene2d.group.add-actor :refer [add-actor!]]
            [clojure.gdx.scene2d.actor.set-opts :as actor]))

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (run! #(add-actor! group %) actors))
  (actor/set-opts! group opts))

(defn create [opts]
  (doto (group/create)
    (set-opts! opts)))
