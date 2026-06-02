(ns clojure.gdx.scene2d.group.create
  (:require [clojure.gdx.scene2d.group.set-opts :refer [set-opts!]])
  (:import (com.badlogic.gdx.scenes.scene2d Group)))

(defn create-group [opts]
  (doto (Group.)
    (set-opts! opts)))
