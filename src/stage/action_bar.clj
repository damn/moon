(ns stage.action-bar
  (:require [clojure.gdx.actor.set-name :as set-name]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [clojure.gdx.button-group.new :as new-button-group]
            [clojure.gdx.button-group.set-max-check-count :as set-max-check-count]
            [clojure.gdx.button-group.set-min-check-count :as set-min-check-count]
            [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [gdx.scenes.scene2d.ui.table :as table]))

(defn create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/new)
                                  (horizontal-group/space 2)
                                  (horizontal-group/pad 2)
                                  (set-name/f "moon.ui.action-bar.horizontal-group")
                                  (set-user-object/f (doto (new-button-group/f)
                                                       (set-max-check-count/f 1)
                                                       (set-min-check-count/f 0))))
                         :expand? true
                         :bottom? true}]]})
    (layout/set-fill-parent true)
    (set-name/f "moon.ui.action-bar")))
