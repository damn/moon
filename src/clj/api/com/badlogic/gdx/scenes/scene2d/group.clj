(ns clj.api.com.badlogic.gdx.scenes.scene2d.group
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn set-opts! [^Group group opts]
  (run! (fn [actor]
          (.addActor group actor))
        (:group/actors opts))
  (actor/set-opts! group opts))

(defn create [opts]
  (doto (Group.)
    (set-opts! opts)))
