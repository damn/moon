(ns gdx.scenes.scene2d.ui.table
  (:require [clojure.gdx.scene2d.ui.table.set-opts :refer [set-opts!]])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn create [opts]
  (doto (Table.)
    (set-opts! opts)))
