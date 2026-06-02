(ns clojure.gdx.scene2d.group.set-opts
  (:require [clojure.gdx.scene2d.actor.set-opts :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn set-opts! [^Group group opts]
  (when-let [actors (:group/actors opts)]
    (run! #(.addActor group %) actors))
  (actor/set-opts! group opts))
