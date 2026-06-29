(ns stage.action-bar
  (:require [scene2d.ui.horizontal-group :as horizontal-group]
            [scene2d.utils.layout.set-fill-parent :refer [set-fill-parent!]]
            [gdx.scenes.scene2d.ui.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ButtonGroup)))

(defn create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/create
                                       {:space 2
                                        :pad 2})
                                  (Actor/.setName "moon.ui.action-bar.horizontal-group")
                                  (Actor/.setUserObject (doto (ButtonGroup.)
                                                          (.setMaxCheckCount 1)
                                                          (.setMinCheckCount 0))))
                         :expand? true
                         :bottom? true}]]})
    (set-fill-parent! true)
    (Actor/.setName "moon.ui.action-bar")))
