(ns ctx.stage.action-bar
  (:require [com.badlogic.gdx.scenes.scene2d.ui.button-group :as button-group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]
            [gdx.scene2d.ui.table :as table]))

(defn create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/new)
                                  (horizontal-group/space! 2)
                                  (horizontal-group/pad! 2)
                                  (actor/set-name! "moon.ui.action-bar.horizontal-group")
                                  (actor/set-user-object! (doto (button-group/new)
                                                       (button-group/set-max-check-count! 1)
                                                       (button-group/set-min-check-count! 0))))
                         :expand? true
                         :bottom? true}]]})
    (layout/set-fill-parent! true)
    (actor/set-name! "moon.ui.action-bar")))
