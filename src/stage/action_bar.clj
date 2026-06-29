(ns stage.action-bar
  (:require [gdx.scenes.scene2d.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup
                                               HorizontalGroup)))

(defn create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (HorizontalGroup.)
                                  (.space 2)
                                  (.pad 2)
                                  (.setName "moon.ui.action-bar.horizontal-group")
                                  (.setUserObject (doto (ButtonGroup.)
                                                    (.setMaxCheckCount 1)
                                                    (.setMinCheckCount 0))))
                         :expand? true
                         :bottom? true}]]})
    (.setFillParent true)
    (Actor/.setName "moon.ui.action-bar")))
