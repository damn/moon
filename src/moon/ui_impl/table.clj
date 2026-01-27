(ns moon.ui-impl.table
  (:require [moon.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn create [opts]
  (-> (Table.)
      (table/set-opts! opts)))
