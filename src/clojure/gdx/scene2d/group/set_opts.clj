(ns clojure.gdx.scene2d.group.set-opts
  (:require [clojure.gdx.scene2d.actor.set-opts :as actor]
            [clojure.gdx.scene2d.group :refer [add-actor!]]))

(defn set-opts! [group opts]
  (when-let [actors (:group/actors opts)]
    (run! #(add-actor! group %) actors))
  (actor/set-opts! group opts))
