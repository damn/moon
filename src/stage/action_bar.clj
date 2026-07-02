(ns stage.action-bar
  (:require [clojure.gdx.actor.set-name :as set-name]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [clojure.gdx.layout.set-fill-parent :as set-fill-parent]
            [gdx.scenes.scene2d.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup
                                               HorizontalGroup)))

(defn create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (HorizontalGroup.)
                                  (.space 2)
                                  (.pad 2)
                                  (set-name/f "moon.ui.action-bar.horizontal-group")
                                  (set-user-object/f (doto (ButtonGroup.)
                                                       (.setMaxCheckCount 1)
                                                       (.setMinCheckCount 0))))
                         :expand? true
                         :bottom? true}]]})
    (set-fill-parent/f true)
    (set-name/f "moon.ui.action-bar")))
