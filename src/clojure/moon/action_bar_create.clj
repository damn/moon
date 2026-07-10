(ns clojure.moon.action-bar-create
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]))

(defn action-bar-create [_ctx]
  (doto (doto (table/new)
    (table-set-opts/set-opts! {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/new)
                                  (horizontal-group/space 2)
                                  (horizontal-group/pad 2)
                                  (actor/setName "moon.ui.action-bar.horizontal-group")
                                  (actor/setUserObject (doto (button-group/new)
                                                       (button-group/setMaxCheckCount 1)
                                                       (button-group/setMinCheckCount 0))))
                         :expand? true
                         :bottom? true}]]}))
    (layout/setFillParent true)
    (actor/setName "moon.ui.action-bar")))
